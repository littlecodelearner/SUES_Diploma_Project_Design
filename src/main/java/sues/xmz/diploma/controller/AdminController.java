package sues.xmz.diploma.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sues.xmz.diploma.common.domain.EmptyPageRespResult;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.exception.users.UsersDeleteFailureException;
import sues.xmz.diploma.domain.dto.users.UserDetailDTO;
import sues.xmz.diploma.domain.req.users.UserDeleteReq;
import sues.xmz.diploma.domain.req.users.UserQueryInBulkByPageReq;
import sues.xmz.diploma.domain.resp.users.UserDetailsPageResult;
import sues.xmz.diploma.service.AdminService;

@Tag(name = "_管理员操作模块", description = "管理员操作相关的API")
@Slf4j
@RequestMapping("/admin")
@RestController
public class AdminController {

    @Resource
    private AdminService adminService;

    @ApiOperationSupport(order = 10)
    @PostMapping("/userDetails")
    @Operation(
            tags = {"01用户相关业务功能",},
            summary = "00用户-分页查看所有用户的详细数据_前端已实现",
            description = "根据用户ID、页码、每页内容量，来获取用户详细信息",
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDetailsPageResult.class))
                    ),
                    @ApiResponse(responseCode = "others", description = "分页查询所有用户的详细数据失败",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmptyPageRespResult.class))
                    )
            }
    )
    public Result<?> listUsersDetailsByPage(@RequestBody @Valid UserQueryInBulkByPageReq userQueryInBulkByPageReq) {
        PageResponse<UserDetailDTO> pageResponse = adminService.listUsersDetailsByPage(
                userQueryInBulkByPageReq.getUsername(),
                userQueryInBulkByPageReq.getCurrent(),
                userQueryInBulkByPageReq.getSize()
        );

        if (pageResponse.getTotal() < 1){
            return Result.success(PageResponse.empty());
        }
        return Result.success(pageResponse);
    }

    @ApiOperationSupport(order = 11)
    @DeleteMapping
    @Operation(
            tags = {"01用户相关业务功能",},
            summary = "00用户-批量删除用户账号_前端已实现",
            description = "根据用户ID，来批量删除用户账号",
            responses = {
                    @ApiResponse(responseCode = "200", description = "删除用户账号成功"),
                    @ApiResponse(responseCode = "500", description = "删除失败，检查传参和后端代码"),
                    @ApiResponse(responseCode = "1000", description = "有些用户不存在"),
                    @ApiResponse(responseCode = "1045", description = "删除用户账号失败"),
            }
    )
    public Result<?> deleteUsersInBulk(@RequestBody @Valid UserDeleteReq userDeleteReq) {
        boolean success = adminService.deleteUsersInBulk(userDeleteReq.getUserIdList());
        if (!success) {
            throw new UsersDeleteFailureException();
        }
        return Result.success("删除用户账号成功");
    }
}
