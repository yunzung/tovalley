package kr.ac.kumoh.illdang100.tovalley.service.lost_found_board;

import kr.ac.kumoh.illdang100.tovalley.domain.lost_found_board.*;
import kr.ac.kumoh.illdang100.tovalley.domain.member.Member;
import kr.ac.kumoh.illdang100.tovalley.domain.member.MemberRepository;
import kr.ac.kumoh.illdang100.tovalley.domain.water_place.WaterPlace;
import kr.ac.kumoh.illdang100.tovalley.domain.water_place.WaterPlaceRepository;
import kr.ac.kumoh.illdang100.tovalley.handler.ex.CustomApiException;
import kr.ac.kumoh.illdang100.tovalley.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.ac.kumoh.illdang100.tovalley.dto.lost_found_board.LostFoundBoardReqDto.*;
import static kr.ac.kumoh.illdang100.tovalley.util.EntityFinder.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LostFoundBoardServiceImpl implements LostFoundBoardService {

    private final LostFoundBoardRepository lostFoundBoardRepository;
    private final MemberRepository memberRepository;
    private final WaterPlaceRepository waterPlaceRepository;
    private final MemberService memberService;

    @Override
    @Transactional
    public Long saveLostFoundBoard(LostFoundBoardSaveReqDto lostFoundBoardSaveReqDto, Long memberId) {
        Member findMember = findMemberByIdOrElseThrowEx(memberRepository, memberId);

        if (memberService.isEmptyMemberNickname(findMember)) {
            throw new CustomApiException("닉네임은 필수 값 입니다");
        }

        WaterPlace findWaterPlace = findWaterPlaceByIdOrElseThrowEx(waterPlaceRepository, lostFoundBoardSaveReqDto.getValleyId());

        LostFoundBoard lostFoundBoard = lostFoundBoardRepository.save(LostFoundBoard.builder()
                .member(findMember)
                .lostFoundEnum(LostFoundEnum.valueOf(lostFoundBoardSaveReqDto.getCategory()))
                .title(lostFoundBoardSaveReqDto.getTitle())
                .content(lostFoundBoardSaveReqDto.getContent())
                .waterPlace(findWaterPlace)
                .build());

        return lostFoundBoard.getId();
    }

    @Override
    @Transactional
    public LostFoundBoard updateLostFoundBoard(LostFoundBoardUpdateReqDto lostFoundBoardUpdateReqDto, Long memberId) {
        LostFoundBoard findLostFoundBoard = findLostFoundBoardByIdWithMemberOrElseThrowEx(lostFoundBoardRepository, lostFoundBoardUpdateReqDto.getLostFoundBoardId());
        checkBoardAccessPermission(findLostFoundBoard, memberId);

        WaterPlace findWaterPlace = findWaterPlaceByIdOrElseThrowEx(waterPlaceRepository, lostFoundBoardUpdateReqDto.getValleyId());
        findLostFoundBoard.updateLostFoundBoard(findWaterPlace, lostFoundBoardUpdateReqDto.getTitle(), lostFoundBoardUpdateReqDto.getContent(), LostFoundEnum.valueOf(lostFoundBoardUpdateReqDto.getCategory()));

        return findLostFoundBoard;
    }

    @Override
    public Boolean isAuthorizedToAccessBoard(LostFoundBoard lostFoundBoard, Long memberId) {
        Member member = lostFoundBoard.getMember();
        return member != null && member.getId().equals(memberId);
    }

    @Override
    @Transactional
    public void updateResolvedStatus(Long lostFoundBoardId, Boolean isResolved, Long memberId) {
        LostFoundBoard findLostFoundBoard = findLostFoundBoardOrElseThrowEx(lostFoundBoardRepository, lostFoundBoardId);
        checkBoardAccessPermission(findLostFoundBoard, memberId);

        findLostFoundBoard.updateResolvedStatus(isResolved);
    }

    @Override
    @Transactional
    public void deleteLostFoundBoard(Long lostFoundBoardId, Long memberId) {
        LostFoundBoard findLostFoundBoard = findLostFoundBoardByIdWithMemberOrElseThrowEx(lostFoundBoardRepository, lostFoundBoardId);
        checkBoardAccessPermission(findLostFoundBoard, memberId);

        lostFoundBoardRepository.delete(findLostFoundBoard);
    }

    private void checkBoardAccessPermission(LostFoundBoard lostFoundBoard, Long memberId) {
        if (!isAuthorizedToAccessBoard(lostFoundBoard, memberId)) {
            throw new CustomApiException("게시글 작성자에게만 권한이 있습니다");
        }
    }
}
