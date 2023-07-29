package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.driver.model.SubscriptionType.*;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Just simply add the user to the Db and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();
        Subscription subscription = user.getSubscription();

        int count=0;
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        for(WebSeries series: webSeriesList){
            if(user.getAge()>=series.getAgeLimit()){
                if(subscription.getSubscriptionType().equals(BASIC)) {
                    if (series.getSubscriptionType().equals(BASIC)) {
                        count++;
                    }
                }
                if(subscription.getSubscriptionType().equals(PRO) ){
                    if (series.getSubscriptionType().equals(BASIC) || series.getSubscriptionType().equals(PRO) ) {
                        count++;
                    }
                }
                if(subscription.getSubscriptionType().equals(ELITE) ){
                    if (series.getSubscriptionType().equals(BASIC) || series.getSubscriptionType().equals(PRO) || series.getSubscriptionType().equals(ELITE) ) {
                        count++;
                    }
                }
            }
        }

        return count;
    }


}
