package com.pinyougou.common;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author wangl
 * 用户名加密
 */
@SuppressWarnings("all")
public class DecryptDruidSource extends DruidDataSource {
    private static String usernamePublicKey="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIiUrZ4iJq7wnhx+Wsb0dRk9JW+HqHObmufhB2NfB9gXXqCYLIb1Oj8J72p1i0yjql5VhX87gqYa3WtCBJcjhU8CAwEAAQ==";

    @Override
    public void setUsername(String username) {
        try {
            username = ConfigTools.decrypt(usernamePublicKey,username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setUsername(username);
    }
}
