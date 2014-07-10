package com.cxstudio.market.updater.persistent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cxstudio.market.pattern.model.CandidatePattern;
import com.cxstudio.market.updater.persistent.mapper.PatternMapper;

public class PatternDao {
	@Autowired
	private PatternMapper mapper;

	@Transactional
	public void insertPattern(CandidatePattern pattern) {

	}
}
