package com.semicolon.ds.controller;

import com.semicolon.ds.core.GnutellaNode;
import com.semicolon.ds.core.ResultsForSearchingQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class NodeController {

    private static final Logger logger = LoggerFactory.getLogger(NodeController.class);

    private final GnutellaNode node;

    public NodeController(GnutellaNode node) {
        this.node = node;
    }

    @GetMapping("/getRoutingTable")
    public ResponseEntity<String> getrouteTable() {
        try {
            String routingTable = node.getRoutingTable();
            return new ResponseEntity<>(routingTable, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred when building object request: {}", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search/{searchQuery}")
    public ResponseEntity<List<ResultsForSearchingQuery>> searchFile(@PathVariable("searchQuery") String searchQuery) {
        try {
            Map<String, ResultsForSearchingQuery> results = node.searchKeywordInUI(searchQuery);
            String output = "";
            if (results.size() == 0){
                return new ResponseEntity("Sorry, no files found", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity(results, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error occurred when building object request: {}", e);
            return new ResponseEntity("Error occurred when building object request: {}", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Map<String, ResultsForSearchingQuery>> downloadFile(@RequestParam("address") String address,
                                                                  @RequestParam ("tcpPort") String tcpPort,
                                                                  @RequestParam ("fileName") String fileName) {
        if (address.equals(null) || address.isEmpty()) {
            return new ResponseEntity("Enter valid address", HttpStatus.BAD_REQUEST);
        }
        if (fileName.equals(null) || fileName.isEmpty()) {
            return new ResponseEntity("Enter valid fileName", HttpStatus.BAD_REQUEST);
        }
        else {
            if (isStringInt(tcpPort)){
                int tcpPortInt = Integer.parseInt(tcpPort);
                node.getFile(address, tcpPortInt, fileName);
                return new ResponseEntity("Downloaded", HttpStatus.OK);
            } else {
                return new ResponseEntity("Enter valid input", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}
