package com.example.dashboard.repository;

import com.example.dashboard.model.CustomerRank;
import com.example.dashboard.model.DailySales;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DashboardRepository {
    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DailySales> dailySalesRowMapper =(rs, rowNum) -> new DailySales(
            rs.getDate("sale_date").toLocalDate(),
            rs.getBigDecimal("total_amount"),
            rs.getInt("order_count")
    );

    private final RowMapper<CustomerRank> customerRankRowMapper =(rs, rowNum) ->
        new CustomerRank(
                rs.getInt("customer_id"),
                rs.getBigDecimal("total_spent"),
                rs.getInt("rank")
        );

    public List<DailySales> findDailySales(){
        return jdbcTemplate.query(
                "select * from daily_sales",
                dailySalesRowMapper
        );
    }
    public List<CustomerRank> findCustomerRank(){
        String sql= """
                select customer_id,
                       total_spent,
                       rank() over (order by total_spent desc) as rank
                from customer_spending
                """;

        return jdbcTemplate.query(sql, customerRankRowMapper);
    }

    public void saveSale(LocalDate saleDate, int customerId, BigDecimal amount) {
        jdbcTemplate.update(
                "INSERT INTO sales (sale_date, customer_id, amount) VALUES (?, ?, ?)",
                saleDate, customerId, amount
        );
    }

}