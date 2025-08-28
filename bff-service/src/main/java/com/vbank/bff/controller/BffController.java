package com.vbank.bff.controller;

import com.vbank.bff.model.TransactionRequest;
import com.vbank.bff.service.BffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BffController {

    @Autowired
    private BffService bffService;

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransactionRequest request) {
        return bffService.initiateTransfer(request);
    }
}