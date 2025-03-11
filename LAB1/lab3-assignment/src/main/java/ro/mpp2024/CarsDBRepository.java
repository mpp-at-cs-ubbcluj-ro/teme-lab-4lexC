package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarsDBRepository implements CarRepository {

    private JdbcUtils dbUtils;



    private static final Logger logger= LogManager.getLogger();

    public CarsDBRepository(Properties props) {
        logger.info("Initializing ro.mpp2024.CarsDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturerN) {
        logger.traceEntry("Finding by manufacter: {}",manufacturerN);
        List<Car> cars = new ArrayList<>();
        Connection con = dbUtils.getConnection();

        try(PreparedStatement preStnt = con.prepareStatement("select * from cars where manufacter = ?")) {
            preStnt.setString(1, manufacturerN);
            try(ResultSet result = preStnt.executeQuery()){
                while(result.next()) {
                    int id = result.getInt("id");
                    String manufacter = result.getString("manufacter");
                    String model = result.getString("model");
                    int year = result.getInt("year");
                    Car car = new Car(manufacter, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB "+ ex);
        }
        logger.traceExit(cars);
        return cars;
    }

    @Override
    public List<Car> findBetweenYears(int min, int max) {
        logger.traceEntry("Finding cars between years: " + min + " - " + max);

        List<Car> cars = new ArrayList<>();
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStnt = con.prepareStatement("SELECT * FROM cars WHERE year BETWEEN ? AND ?")) {
            preStnt.setInt(1, min);
            preStnt.setInt(2, max);
            try (ResultSet result = preStnt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String manufacter = result.getString("manufacter");
                    String model = result.getString("model");
                    int year = result.getInt("year");
                    Car car = new Car(manufacter, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }

        logger.traceExit(cars);
        return cars;
    }

    @Override
    public void add(Car elem) {
        logger.trace("Saving task with Id=" + elem.getId() + " " + elem.getManufacturer() + " " + elem.getModel() + " " + elem.getYear());

        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStnt=con.prepareStatement("insert into cars (manufacter, model, year) values (?, ?, ?) ")){

                preStnt.setString(1, elem.getManufacturer());
                preStnt.setString(2, elem.getModel());
                preStnt.setInt(3, elem.getYear());
                int result = preStnt.executeUpdate();
                logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Car elem) {
        logger.traceEntry("Updating car with ID {} to {}", integer, elem);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStnt = con.prepareStatement("UPDATE cars SET manufacter = ?, model = ?, year = ? WHERE id = ?")) {
            preStnt.setString(1, elem.getManufacturer());
            preStnt.setString(2, elem.getModel());
            preStnt.setInt(3, elem.getYear());
            preStnt.setInt(4, integer);
            int result = preStnt.executeUpdate();
            logger.trace("Updated {} instance(s)", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public Iterable<Car> findAll() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<Car> cars = new ArrayList<>();
        try(PreparedStatement preStnt = con.prepareStatement("select * from cars")){
            try(ResultSet result = preStnt.executeQuery()) {
                while(result.next()){
                    int id = result.getInt("id");
                    String manufacter = result.getString("manufacter");
                    String model = result.getString("model");
                    int year = result.getInt("year");
                    Car car = new Car(manufacter, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit(cars);
        return cars;
    }
}
