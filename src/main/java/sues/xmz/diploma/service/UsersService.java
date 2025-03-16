package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.domain.po.Users;
import sues.xmz.diploma.domain.req.users.PasswordChangeReq;
import sues.xmz.diploma.domain.req.users.UserInfoUpdateReq;
import sues.xmz.diploma.domain.req.users.UserLoginReq;
import sues.xmz.diploma.domain.req.users.UserRegisterReq;
import sues.xmz.diploma.domain.resp.users.UserInfoResp;

public interface UsersService extends IService<Users>{

    boolean register(UserRegisterReq userRegisterReq);

    UserInfoResp login(UserLoginReq userLoginReq);

    boolean updateInfo(UserInfoUpdateReq userInfoUpdateReq);

    boolean changePassword(PasswordChangeReq passwordChangeReq);

}
