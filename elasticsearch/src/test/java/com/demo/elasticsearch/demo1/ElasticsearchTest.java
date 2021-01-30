package com.demo.elasticsearch.demo1;

import com.demo.elasticsearch.model.Item;
import com.demo.elasticsearch.repository.ItemRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @auther: huanghonglin
 * @date: 2021/1/30
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ItemRepository itemRepository;

    /**
     * 创建索引
     */
    @Test
    public void testCreateIndex(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Item.class);
        // indexOperations.create();
        Document mapping = indexOperations.createMapping();
        indexOperations.putMapping(mapping);
    }

    /**
     * 删除索引
     */
    @Test
    public void testDeleteIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Item.class);
        indexOperations.delete();
    }

    /**
     * 新增文档
     */
    @Test
    public void testCreate() {
        Item item = new Item(1L, "小米手机7", " 手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg");
        itemRepository.save(item);
    }

    /**
     * 删除文档
     */
    @Test
    public void testDelete() {
        itemRepository.deleteById(1L);
    }

    /**
     * 查询全部
     */
    @Test
    public void testFind() {
        Iterable<Item> items = itemRepository.findAll(Sort.by("price").descending());
        items.forEach(System.out::println);
    }

    /**
     * 条件查询
     */
    @Test
    public void testFindByTitle() {
        List<Item> items = itemRepository.findByTitle("手机");
        items.forEach(System.out::println);
    }

    /**
     * 条件查询
     */
    @Test
    public void testFindByPriceBetween() {
        List<Item> items = itemRepository.findByPriceBetween(3699D, 4499D);
        items.forEach(System.out::println);
    }

    /**
     * 高级查询
     */
    @Test
    public void testSearch() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "手机"));
        int page = 0;
        int size = 1;
        queryBuilder.withPageable(PageRequest.of(page, size));
        SearchHits<Item> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Item.class);
        List<Item> items = hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        items.forEach(System.out::println);
    }

    /**
     * 聚合查询
     */
    @Test
    public void testAgg() {
        // 初始化自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加聚合，嵌套聚合求平均值
        queryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brand")
                .subAggregation(AggregationBuilders.avg("price_avg").field("price")));
        // 添加结果集过滤，不包括任何字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        // 执行聚合查询，获取分组数据
        SearchHits<Item> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), Item.class);
        Terms terms = (Terms) searchHits.getAggregations().asMap().get("brandAgg");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
            Map<String, Aggregation> stringAggregationMap = bucket.getAggregations().asMap();
            Avg price_avg = (Avg) stringAggregationMap.get("price_avg");
            System.out.println(price_avg.getValue());
        });
    }
}

