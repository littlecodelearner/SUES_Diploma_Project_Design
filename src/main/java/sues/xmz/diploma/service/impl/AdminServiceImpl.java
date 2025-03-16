package sues.xmz.diploma.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.exception.users.UserNotFoundException;
import sues.xmz.diploma.domain.dto.users.UserDetailDTO;
import sues.xmz.diploma.domain.po.Users;
import sues.xmz.diploma.service.AdminService;
import sues.xmz.diploma.service.UsersService;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private UsersService userService;

    @Override
    public PageResponse<UserDetailDTO> listUsersDetailsByPage(String userName, Long current, Long size) {
        Page<Users> usersPage = userService.lambdaQuery()
                .like(userName != null && !userName.isBlank(),Users::getUsername, userName)
                .page(Page.of(current, size));

        return PageResponse.getPageResponse(usersPage,UserDetailDTO.class);
    }


    @Override
    public boolean deleteUsersInBulk(List<Integer> userIdList) {
        List<Users> users = userService.listByIds(userIdList);
        if (userIdList.size() != users.size()) {
            throw new UserNotFoundException();
        }

        return userService.removeByIds(userIdList);
    }

}
