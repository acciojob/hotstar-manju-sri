package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.exception.AlreadyUpdatedException;
import com.driver.exception.UserNotExistException;
import com.driver.model.Subscription;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.driver.model.SubscriptionType.*;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Optional<User> optionalUser = userRepository.findById(subscriptionEntryDto.getUserId());
        if(optionalUser.isEmpty()){
            throw new UserNotExistException("Invalid User Id");
        }

        User savedUser = optionalUser.get();

        int totalAmount=0;
        if(subscriptionEntryDto.getSubscriptionType()==BASIC) {
            totalAmount = 500+ (200*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else if(subscriptionEntryDto.getSubscriptionType()==PRO) {
            totalAmount = 800+ (250*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else if(subscriptionEntryDto.getSubscriptionType()==ELITE) {
            totalAmount = 1000+ (350*subscriptionEntryDto.getNoOfScreensRequired());
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setUser(savedUser);
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setTotalAmountPaid(totalAmount);

        subscriptionRepository.save(subscription);

        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
          Optional<User> optionalUse = userRepository.findById(userId);
          User user = optionalUse.get();
          Subscription subscription = user.getSubscription();

          int priceDiff = 0;
          if (subscription.getSubscriptionType() == ELITE) {
              throw new AlreadyUpdatedException("User Already Use Elite Subscription");
          } else if (subscription.getSubscriptionType() == BASIC) {
              subscription.setSubscriptionType(PRO);
              int revisedAmount = 800 + (250 * subscription.getNoOfScreensSubscribed());
              priceDiff = revisedAmount - subscription.getTotalAmountPaid();
              subscription.setTotalAmountPaid(revisedAmount);

          } else if (subscription.getSubscriptionType() == PRO) {
              subscription.setSubscriptionType(ELITE);
              int revisedAmount = 1000 + (350 * subscription.getNoOfScreensSubscribed());
              priceDiff = revisedAmount - subscription.getTotalAmountPaid();
              subscription.setTotalAmountPaid(revisedAmount);
          }
          subscriptionRepository.save(subscription);
          return priceDiff;
    }

    public Integer calculateTotalRevenueOfHotstar() throws Exception{

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        int revenue = 0;
        for(Subscription subscription: subscriptionList){
            revenue = revenue + subscription.getTotalAmountPaid();
        }
        return revenue;
    }

}
