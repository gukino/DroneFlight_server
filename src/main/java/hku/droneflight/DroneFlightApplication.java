package hku.droneflight;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("hku.droneflight.mapper")
public class DroneFlightApplication {



    public static void main(String[] args) {
        SpringApplication.run(DroneFlightApplication.class, args);
    }

}
