package com.cxstudio.market.updater.dataprovider;

import com.cxstudio.market.updater.model.Trade;

public interface LineDataParser {

	public abstract Trade parse(String textline);

}