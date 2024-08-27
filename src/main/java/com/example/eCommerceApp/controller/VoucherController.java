package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.voucher.VoucherInput;
import com.example.eCommerceApp.dto.voucher.VoucherOutput;
import com.example.eCommerceApp.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/voucher")
public class VoucherController {
    private final VoucherService voucherService;

    @Operation(summary = "Thêm voucher shop")
    @PostMapping("/create-voucher-shop")
    public void createVoucherShop(@RequestHeader("Authorization") String accessToken,
                                  @RequestBody VoucherInput voucherInput) {
        voucherService.createVoucherShop(accessToken,voucherInput);
    }

    @Operation(summary = "Tạo voucher product template")
    @PostMapping("/create-voucher-product-template")
    public void createVoucherProductTemplate(@RequestHeader("Authorization") String accessToken,
                                             @RequestBody VoucherInput voucherInput,
                                             @RequestParam Long productTemplateId) {
        voucherService.createVoucherProductTemplate(accessToken, voucherInput, productTemplateId);
    }

    @Operation(summary = "Thêm voucher product")
    @PostMapping("/create-voucher-product")
    public void createVoucherProduct(@RequestHeader("Authorization") String accessToken,
                                     @RequestBody List<VoucherInput> voucherInputs) {
        voucherService.createVoucherProduct(accessToken,voucherInputs);
    }

    @Operation(summary = "Lấy ra voucher shop")
    @GetMapping("/get-vouchers-shop")
    public List<VoucherOutput> getVouchersShop(@RequestParam Long shopId) {
        return voucherService.getVouchersShop(shopId);
    }

    @Operation(summary = "Lấy ra voucher product")
    @GetMapping("/get-vouchers-product")
    public List<VoucherOutput> getVoucherProducts(@RequestParam Long productTemplateId) {
        return voucherService.getVoucherProducts(productTemplateId);
    }

    @Operation(summary = "Lấy ra voucher product template")
    @GetMapping("/get-vouchers-product-template")
    public VoucherOutput getVoucherProductTemplate(@RequestParam Long productTemplateId) {
        return voucherService.getVoucherProductTemplate(productTemplateId);
    }

    @Operation(summary = "Xóa voucher")
    @DeleteMapping("/delete-voucher")
    public void deleteVoucher(@RequestParam Long voucherId,
                              @RequestHeader("Authorization") String accessToken) {
        voucherService.deleteVoucher(accessToken, voucherId);
    }

    @Operation(summary = "Người dùng thêm voucher shop vào kho voucher")
    @PostMapping("add-voucher")
    public void addVoucherShop(@RequestHeader("Authorization") String accessToken,
                               @RequestParam Long shopId,
                               @RequestParam Long voucherId) {
        voucherService.addVoucherShop(accessToken,shopId,voucherId);
    }

    @Operation(summary = "Xem kho voucher")
    @GetMapping("get-vouchers")
    public List<VoucherOutput> getVouchersBy(@RequestHeader("Authorization") String accessToken) {
        return voucherService.getVouchersBy(accessToken);
    }
}
