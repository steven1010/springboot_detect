package com.skspruce.ism.fm.backend.service;

import com.skspruce.ism.fm.backend.entity.FmAlarmActive;
import com.skspruce.ism.fm.backend.entity.FmAlarmHistory;
import com.skspruce.ism.fm.backend.repository.FmAlarmActiveRepository;
import com.skspruce.ism.fm.backend.repository.FmAlarmHistoryRepository;
import com.skspruce.ism.fm.backend.repository.FmRuleAutoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化加载
 */
@Component
public class ApplicationStartup implements  CommandLineRunner{

	@Autowired
	private FmRuleAutoRepository fmRuleAutoRepository;
	
	@Autowired
	private FmAlarmActiveRepository fmAlarmActiveRepository;
	
	@Autowired
	private FmAlarmHistoryRepository fmAlarmHistoryRepository;

	
	@Override
	public void run(String... args) throws Exception {
		
		
		System.out.println(fmRuleAutoRepository.findAll().size());

		Thread.sleep(8000);

		System.out.println(fmRuleAutoRepository.findAll().size());

		Thread.sleep(15000);

		System.out.println(fmRuleAutoRepository.findAll().size());

		Thread.sleep(8000);

		System.out.println(fmRuleAutoRepository.findAll().size());
	}
}