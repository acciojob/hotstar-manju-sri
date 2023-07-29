package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.exception.ProductionHouseNotExistException;
import com.driver.exception.WebseriesAlreadyExistException;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries optionalWebSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());

        if(optionalWebSeries != null){
            throw new WebseriesAlreadyExistException("Webseries Already Exist");
        }

        Optional<ProductionHouse> optionalProductionHouse =
                productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());

        if(optionalProductionHouse.isEmpty()){
            throw new ProductionHouseNotExistException("Invalid Production Id");
        }

        ProductionHouse productionHouse = optionalProductionHouse.get();


        WebSeries webSeries1 = new WebSeries();     //convert dto to entity
        webSeries1.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries1.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries1.setRating(webSeriesEntryDto.getRating());
        webSeries1.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries1.setProductionHouse(productionHouse);
        WebSeries savedWebservice = webSeriesRepository.save(webSeries1);

        productionHouse.getWebSeriesList().add(webSeries1);
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();

        double total = 0;
        for(WebSeries web : webSeriesList){
            total = web.getRating()+total;
        }

        double averageRating = (total+savedWebservice.getRating())/(webSeriesList.size()+1);

        productionHouse.setRatings(averageRating);
        productionHouseRepository.save(productionHouse);

        return savedWebservice.getId();
    }

}
