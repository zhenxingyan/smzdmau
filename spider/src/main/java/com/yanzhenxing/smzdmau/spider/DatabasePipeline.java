package com.yanzhenxing.smzdmau.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Jason Yan
 * @date 21/04/2019
 */
public class DatabasePipeline implements Pipeline {

    private static Logger logger = LoggerFactory.getLogger(DatabasePipeline.class);

    private Connection conn = null;

    public DatabasePipeline(Connection connection) {
        this.conn = connection;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        List<Product> products = resultItems.get("products");

        if (products != null && products.size() > 0) {
            PreparedStatement stmt = null;

            try {
                String sql = "insert into product(name,category,image,price,save) values(?,?,?,?,?);";
                stmt = conn.prepareStatement(sql);

                for (Product product : products) {
                    stmt.setString(1,product.getName());
                    stmt.setString(2,"vitamins");
                    stmt.setString(3,product.getImage());
                    stmt.setFloat(4,product.getPrice());
                    stmt.setFloat(5,product.getSave());

                    stmt.executeUpdate();
//                    stmt.addBatch();
                }
//                stmt.executeBatch();

                stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(stmt!=null){
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        }


    }
}
