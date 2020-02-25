package com.yanzhenxing.smzdmau.spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jason Yan
 * @date 20/04/2019
 */
public class ChemistWarehousePageProcessor implements PageProcessor {

    private static Logger logger = LoggerFactory.getLogger(ChemistWarehousePageProcessor.class);

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    private Pattern pricePattern = Pattern.compile("\\$\\d{0,6}(\\.\\d{0,2})?");

    private Integer maxPageNumber = null;
    private Integer currentPageNumber = 1;

    public static final String searchUrl = "https://www.chemistwarehouse.com.au/shop-online/81/vitamins";

    private static final String productCategory = "vitamins";
    private static final String dbUrl = "jdbc:mysql://localhost/smzdmau?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true";
    private static final String username = "yanzhenxing";
    private static final String password = "870624abcdE#";

    @Override
    public void process(Page page) {
        //test parse html element here
        // get total product page number

        logger.info("===============");
        logger.info("current page: " + currentPageNumber);
        logger.info("===============");

        if (maxPageNumber == null) {
            List<String> lastPageLinks = page.getHtml().css(".last-page").links().all();
            if (lastPageLinks != null && lastPageLinks.size() > 0) {
                String lastPageUrl = lastPageLinks.get(0);
                if (lastPageUrl.contains("page=")) {
                    maxPageNumber = Integer.parseInt(lastPageUrl.substring(lastPageUrl.indexOf("page=") + 5));
                } else {
                    maxPageNumber = 1;
                }
            }
            page.addTargetRequests(new ArrayList<String>() {{
                add(searchUrl + "?page=" + currentPageNumber);
            }});
            currentPageNumber++;
        } else if (currentPageNumber < maxPageNumber) {
            page.addTargetRequests(new ArrayList<String>() {{
                add(searchUrl + "?page=" + currentPageNumber);
            }});

            currentPageNumber++;
        }

        // parse product info from page
        List<String> productElements = page.getHtml().css(".Product").all();
        if (!CollectionUtil.isEmpty(productElements)) {
            List<Product> products = new ArrayList<>();
            for (String productElement : productElements) {
                Product product = assembleProduct(productElement);
                if (product != null) {
                    products.add(product);
                }
            }

            products.forEach(System.out::println);

            page.putField("products", products);
        }


    }

    private Product assembleProduct(String docStr) {
        try {
            Product product = new Product();

            Document document = Jsoup.parse(docStr);

            // parse image url
            Element productImage = document.select(".product-image img").first();
            product.setImage(productImage.attr("src"));

            // parse product name
            product.setName(productImage.attr("alt"));
//            Element productName = document.select(".product-name").first();
//            product.setName(productName.text());

            // parse product price
            Element productPrice = document.select(".Price").first();
            Matcher priceMatcher = pricePattern.matcher(productPrice.text());
            if (priceMatcher.find()) {
                product.setPrice(Float.parseFloat(priceMatcher.group(0).substring(1)));
            }

            // parse product save
            Element productSave = document.select(".Save").first();
            if (productSave != null && productSave.text().length() > 0) {
                Matcher saveMatcher = pricePattern.matcher(productSave.text());
                if (saveMatcher.find()) {
                    product.setSave(Float.parseFloat(saveMatcher.group(0).substring(1)));
                }
            }

            // set product category
            product.setCategory(productCategory);

            // set product brand

            return product;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Parse product error, str: {}", docStr);
        }
        return null;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Connection conn = null;

        try {
//            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, username, password);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        final Connection finalConn = conn;
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                if(finalConn != null){
                    try {
                        finalConn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Pipeline dbPipeline = new DatabasePipeline(conn);

        Spider.create(new ChemistWarehousePageProcessor()).addUrl(searchUrl).addPipeline(dbPipeline).thread(1).run();


    }
}
