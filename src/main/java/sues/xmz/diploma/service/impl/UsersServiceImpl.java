package sues.xmz.diploma.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.exception.users.*;
import sues.xmz.diploma.common.utils.PasswordUtil;
import sues.xmz.diploma.domain.po.Users;
import sues.xmz.diploma.domain.req.users.PasswordChangeReq;
import sues.xmz.diploma.domain.req.users.UserInfoUpdateReq;
import sues.xmz.diploma.domain.req.users.UserLoginReq;
import sues.xmz.diploma.domain.req.users.UserRegisterReq;
import sues.xmz.diploma.domain.resp.users.UserInfoResp;
import sues.xmz.diploma.mapper.UsersMapper;
import sues.xmz.diploma.service.UsersService;

import java.util.Objects;

@Service
@Transactional
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {


    @Override
    public boolean register(UserRegisterReq userRegisterReq) {
        // 检查用户名是否存在
        if (this.count(new LambdaQueryWrapper<Users>().eq(Users::getUsername, userRegisterReq.getUsername())) > 0) {
            throw new UsernameAlreadyExistsException();
        }
        // 检查邮箱是否存在
        if (this.count(new LambdaQueryWrapper<Users>().eq(Users::getEmail, userRegisterReq.getEmail())) > 0) {
            throw new EmailAlreadyExistsException();
        }

        // 加密密码
        userRegisterReq.setPasswordHash(PasswordUtil.encodePassword(userRegisterReq.getPasswordHash()));
        // 保存用户
        return this.save(BeanUtil.copyProperties(userRegisterReq, Users.class));
    }

    @Override
    public UserInfoResp login(UserLoginReq userLoginReq) {
        // 查询用户
        Users user = this.getOne(new LambdaQueryWrapper<Users>().eq(Users::getUsername, userLoginReq.getUsername()));

        // 验证用户是否存在
        if (null == user) {
            throw new UserNotFoundException();
        }
        // 验证密码
        if (PasswordUtil.verifyNotOldPassword(userLoginReq.getPasswordHash(), user.getPasswordHash())) {
            throw new PasswordErrorException();
        }

        // 返回用户信息
        return BeanUtil.copyProperties(user, UserInfoResp.class);
    }

    @Override
    public boolean updateInfo(UserInfoUpdateReq userInfoUpdateReq) {
        Users updateUser = BeanUtil.copyProperties(userInfoUpdateReq, Users.class);
        // 检查邮箱是否是自己的
        Users myself = this.getById(updateUser.getUserId());
        if (!Objects.equals(myself.getEmail(), updateUser.getEmail())
                && this.count(new LambdaQueryWrapper<Users>().eq(Users::getEmail, updateUser.getEmail())) > 0
        ) {
            throw new EmailAlreadyExistsException();
        }

        // 只更新非敏感信息
        return this.updateById(updateUser);
    }

    @Override
    public boolean changePassword(PasswordChangeReq passwordChangeReq) {
        Users user = this.getById(passwordChangeReq.getUserId());
        if (user == null) {
            throw new UserNotFoundException();
        }
        // 验证旧密码
        if (PasswordUtil.verifyNotOldPassword(passwordChangeReq.getOldPassword(), user.getPasswordHash())) {
            throw new IncorrectOldPasswordException();
        }
        // 设置新密码
        user.setPasswordHash(PasswordUtil.encodePassword(passwordChangeReq.getNewPassword()));
        return this.updateById(user);
    }

}
