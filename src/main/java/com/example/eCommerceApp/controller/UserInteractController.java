package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.comment.CommentInput;
import com.example.eCommerceApp.dto.comment.CommentOutput;
import com.example.eCommerceApp.service.UserInteractService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/user/interact")
public class UserInteractController {
    private final UserInteractService userInteractService;

    @Operation(summary = "Comment sản phẩm")
    @PostMapping("/comment")
    public void comment(@RequestHeader("Authorization") String accessToken,
                        @RequestParam Long productTemplateId,
                        @RequestBody CommentInput commentInput) {
        userInteractService.comment(accessToken,productTemplateId,commentInput);
    }

    @Operation(summary = "Xóa comment")
    @PostMapping("/remove-comment")
    public void removeComment(@RequestHeader("Authorization") String accessToken,
                              @RequestParam Long commentId) {
        userInteractService.removeComment(accessToken,commentId);
    }

    @Operation(summary = "Thích sản phẩm")
    @PostMapping("/like-product")
    public void likeProduct(@RequestHeader("Authorization") String accessToken,
                            @RequestParam Long productId) {
        userInteractService.likeProduct(accessToken,productId);
    }

    @Operation(summary = "Lấy ra comment cho sản phẩm")
    @GetMapping("/get-comment")
    public Page<CommentOutput> getCommentsOfProduct(@RequestParam Long productTemplateId,
                                                    @ParameterObject Pageable pageable) {
        return userInteractService.getCommentsOfProduct(productTemplateId,pageable);
    }

    @Operation(summary = "Theo dõi shop")
    @PostMapping("/follow-shop")
    public void followShop(@RequestHeader("Authorization") String accessToken,
                           @RequestParam Long shopId) {
        userInteractService.followShop(accessToken,shopId);
    }
}
