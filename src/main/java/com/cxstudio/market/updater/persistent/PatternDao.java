package com.cxstudio.market.updater.persistent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cxstudio.market.pattern.model.CandidatePattern;
import com.cxstudio.market.updater.persistent.mapper.PatternMapper;

public class PatternDao {
	@Autowired
	private PatternMapper mapper;

	public List<CandidatePattern> getPatterns() {
		return mapper.selectPatterns();
	}

	@Transactional
	public void insertPattern(CandidatePattern pattern) {
		mapper.insertPattern(pattern);
		mapper.insertSteps(pattern.getPatternId(), pattern.getSteps());
	}
}
