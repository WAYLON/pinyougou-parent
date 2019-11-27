package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 微信支付接口
 * @author Administrator
 *
 */
public interface WeixinPayService {

	/**
	 * 生成微信支付二维码
	 * @param out_trade_no 订单号
	 * @param total_fee 金额(分)
	 * @return
	 */
	public Map createNative(String out_trade_no, String total_fee);
}
