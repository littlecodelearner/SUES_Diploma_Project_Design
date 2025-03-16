package sues.xmz.diploma.service;

import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.domain.dto.users.UserDetailDTO;

import java.util.List;

public interface AdminService {

    PageResponse<UserDetailDTO> listUsersDetailsByPage(String userName, Long current, Long size);

    boolean deleteUsersInBulk(List<Integer> userIdList);

}
