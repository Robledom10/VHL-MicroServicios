package com.vhl.reservationservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Component
public class PackageServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(PackageServiceClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.package.url}")
    private String packageServiceUrl;

    public PackageInfo getPackageInfo(Long packageId) {
        try {
            String url = packageServiceUrl + "/api/packages/" + packageId;
            logger.info("Consultando información del paquete: {}", url);
            
            PackageInfo packageInfo = restTemplate.getForObject(url, PackageInfo.class);
            
            if (packageInfo != null) {
                logger.info("Paquete encontrado: {}", packageInfo.getId());
                return packageInfo;
            }
        } catch (RestClientException e) {
            logger.error("Error al consultar el paquete {}: {}", packageId, e.getMessage());
        }
        return null;
    }

    public boolean validateAvailableSpots(Long packageId, Integer requiredSpots) {
        try {
            PackageInfo packageInfo = getPackageInfo(packageId);
            if (packageInfo != null && packageInfo.getAvailableSpots() >= requiredSpots) {
                logger.info("Cupos disponibles validados para paquete: {}", packageId);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error validando cupos: {}", e.getMessage());
        }
        return false;
    }

    // Inner class for Package Information
    public static class PackageInfo {
        private Long id;
        private String name;
        private String description;
        private Integer totalSpots;
        private Integer availableSpots;
        private Double price;

        // Constructors
        public PackageInfo() {}

        public PackageInfo(Long id, String name, Integer totalSpots, Integer availableSpots, Double price) {
            this.id = id;
            this.name = name;
            this.totalSpots = totalSpots;
            this.availableSpots = availableSpots;
            this.price = price;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getTotalSpots() {
            return totalSpots;
        }

        public void setTotalSpots(Integer totalSpots) {
            this.totalSpots = totalSpots;
        }

        public Integer getAvailableSpots() {
            return availableSpots;
        }

        public void setAvailableSpots(Integer availableSpots) {
            this.availableSpots = availableSpots;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }
}
