package com.yash.controller;

import com.yash.entity.Products;
import com.yash.entity.SellerProfile;
import com.yash.enums.ProductStatus;
import com.yash.repository.ProductsRepository;
import com.yash.repository.SellerRepository;
import com.yash.service.BackblazeService;
import com.yash.util.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private BackblazeService backblazeService;

    @Autowired
    private SellerRepository sellerRepository;

    @PostMapping(value = "seller",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProductsToSeller(
            @RequestParam Long sellerId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam Integer stockQuantity,
            @RequestParam String category,
            @RequestParam ProductStatus status){

        try {
            System.out.println("in products");

            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "File is empty"
                        ));
            }

            // Validate file size (max 50MB)
            long maxSize = 50 * 1024 * 1024; // 50MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "File size exceeds maximum limit of 50MB"
                        ));
            }

            SellerProfile seller = sellerRepository.findById(sellerId).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Seller not found"));

            String fileUrl = backblazeService.uploadFile(file);
            String outputFile = UtilsFunction.extractFileName(fileUrl);
            String finalFile = backblazeService.generateSignedUrl(outputFile);

            if(seller != null){
                Products p = new Products();
                p.setSeller(seller);
                p.setImageUrl(finalFile);
                p.setCategory(category);
                p.setStockQuantity(stockQuantity);
                p.setPrice(price);
                p.setDescription(description);
                p.setTitle(title);
                p.setStatus(status);

                productsRepository.save(p);


            }

            return ResponseEntity.ok("product saved");



        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to upload file: " + e.getMessage()
                    ));
        }


    }

    @GetMapping("/image-url/{fileName}")
    public String getImageUrl(@PathVariable String fileName){
        return backblazeService.generateSignedUrl(fileName);
    }

}
