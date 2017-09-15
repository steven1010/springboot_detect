package com.skspruce.ism.detect.webapi.strategy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化加载
 */
@Component
public class ApplicationStartup implements  CommandLineRunner{

	@Autowired
	private AuditDetectService auditDetectService;

	@Override
	public void run(String... args) throws Exception {
//		auditDetectService.findAuditDetect("048B4224132E006CFDAFEB251503950367");
	}
}