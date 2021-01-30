package com.demo.elasticsearch.repository;

import com.demo.elasticsearch.model.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @auther: huanghonglin
 * @date: 2021/1/30
 */
public interface ItemRepository extends ElasticsearchRepository<Item, Long> {

    List<Item> findByTitle(String title);

    List<Item> findByPriceBetween(Double d1,Double d2);
}
