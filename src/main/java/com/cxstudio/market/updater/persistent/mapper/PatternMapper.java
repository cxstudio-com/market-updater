package com.cxstudio.market.updater.persistent.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cxstudio.market.pattern.model.CandidatePattern;
import com.cxstudio.market.pattern.model.PatternQualifier;
import com.cxstudio.market.pattern.model.Step;

public interface PatternMapper {

	List<CandidatePattern> selectPatterns(@Param("qualifier") PatternQualifier qualifier);

	void insertSteps(@Param("patternId") long patternId, @Param("steps") List<Step> steps);

	void insertPattern(@Param("pattern") CandidatePattern pattern);
}
