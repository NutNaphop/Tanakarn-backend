package com.tanakarn.backend.transfer.controller;

import com.tanakarn.backend.account.service.AccountService;
import com.tanakarn.backend.common.response.ApiResponse;
import com.tanakarn.backend.transfer.dto.request.TransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transfer")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class TransferController {
    private final AccountService accountService;

    public TransferController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Transfer money between accounts")
    @PostMapping("/api/transfer")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> transferMoney(@RequestBody TransferRequest request) {
        try {

            accountService.transferMoney(
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    request.getAmount()
            );
            return ResponseEntity.ok().body(new ApiResponse<>(true, "Transfer successful", null));
        } catch (RuntimeException e) {
            // ถ้าเชฟบอกว่าปรุงไม่ได้ (เช่น เงินไม่พอ) ก็ส่งเหตุผลกลับไป
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

}
