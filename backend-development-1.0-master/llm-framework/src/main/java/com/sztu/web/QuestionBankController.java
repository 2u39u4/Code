package com.sztu.web;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sztu.context.BaseContext;
import com.sztu.entity.Collection;
import com.sztu.entity.QuestionBank;
import com.sztu.entity.User;
import com.sztu.result.Result;
import com.sztu.service.CollectionService;
import com.sztu.service.QuestionBankService;
import com.sztu.service.UserService;
import com.sztu.vo.QuestionBankVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/question")
public class QuestionBankController {
    @Autowired
    private QuestionBankService questionBankService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private UserService userService;
    /***
     * 根据题号查询题目答案
     * @param
     * @return
     */
    @PostMapping("/search/{name}")
    public Result<List<QuestionBankVo>> searchQuestion(@PathVariable("name") String name){
        LambdaQueryWrapper<QuestionBank> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(QuestionBank::getName, name).select(QuestionBank::getId,QuestionBank::getName);
        List<QuestionBank> questionBanks = questionBankService.getBaseMapper().selectList(queryWrapper);
        List<QuestionBankVo> res = new ArrayList<>();
        if (questionBanks == null || questionBanks.isEmpty()) {
            return Result.success(res);
        }

        questionBanks.forEach((q)-> {
            QuestionBankVo questionBankVo = new QuestionBankVo();
            BeanUtils.copyProperties(q, questionBankVo);
            res.add(questionBankVo);
            //log.info("题目：{}", questionBankVo);
        });
        //log.info("题目：{}", res);
        return Result.success(res);
    }
    @GetMapping
    public Result<QuestionBank> getQuestionBank(@RequestParam("id") Long id) {
        return Result.success(questionBankService.getById(id));
    }
    @PutMapping("/{questionId}")
    public Result<?> CreateNewCollection(@PathVariable("questionId") Long questionId) {

        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Collection> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Collection::getUserId, userId).eq(Collection::getQuestionId, questionId);
        int count = collectionService.count(queryWrapper);
        if (count > 0) {
            return Result.error("您已收藏该题");
        }
        // 未收藏过该题目，创建新的收藏记录
        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setQuestionId(questionId);
        collectionService.save(collection);
        return Result.success("收藏成功");
    }

    @GetMapping("/{studentId}")
    public Result<List<QuestionBank>> getQuestionBankCollection(@PathVariable("studentId") Long studentId) {
        // 根据 studentId 查询用户信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getStudentId, studentId);
        log.info("studentId: " + studentId);

        User user = userService.getOne(userWrapper);
        if (user == null || user.getId() == null) {
            return Result.error("用户不存在");
        }

        Long userId = Long.valueOf(user.getId());
        log.info("userId: " + userId);

        // 查询用户的收藏记录
        LambdaQueryWrapper<Collection> collectionWrapper = new LambdaQueryWrapper<>();
        collectionWrapper.eq(Collection::getUserId, userId);
        List<Collection> collections = collectionService.list(collectionWrapper);

        // 如果没有收藏记录，直接返回空列表
        if (collections == null || collections.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 提取收藏的题目 ID
        List<Long> idList = new ArrayList<>();
        for (Collection collection : collections) {
            idList.add(collection.getQuestionId());
        }

        // 检查 idList 是否为空
        if (idList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 查询对应的题目信息
        List<QuestionBank> questionBanks = questionBankService.getBaseMapper().selectBatchIds(idList);
        return Result.success(questionBanks);
    }

    //判断是否有收藏
    @GetMapping("/isCollected/{questionId}")
    public Result<Boolean> isCollected(@PathVariable("questionId") Long questionId){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Collection> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Collection::getUserId, userId).eq(Collection::getQuestionId, questionId);
        int count = collectionService.count(queryWrapper);
        if (count > 0) {
            return Result.success(true);
        }
        return Result.success(false);
    }

    //取消收藏
    @DeleteMapping("/cancelCollection/{id}")
    public Result<?> CancelCollection(@PathVariable("id") Long questionId) {
        Long userId = BaseContext.getCurrentId();
        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setQuestionId(questionId);
        // 检查是否已经收藏
        Result<Boolean> isCollected = isCollected(questionId);
        if (isCollected.getData()) {
            // 如果已经收藏，删除收藏记录
            LambdaQueryWrapper<Collection> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(Collection::getUserId, userId).eq(Collection::getQuestionId, questionId);
            boolean removed = collectionService.remove(deleteWrapper);
            if (removed) {
                return Result.success("取消收藏成功");
            } else {
                return Result.error("取消收藏失败");
            }
        } else {
            return Result.error("该题目未被收藏");
        }
    }
}
