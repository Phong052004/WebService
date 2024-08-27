package com.example.eCommerceApp.service;

import com.example.eCommerceApp.repository.ShippingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ShippingService {
    private final ShippingRepository shippingRepository;
}
