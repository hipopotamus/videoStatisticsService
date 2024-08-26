package statisticsservice.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    GLOBAL_EXCEPTION(400, "잘못된 요청입니다.", "000"),
    BIND_EXCEPTION(400, "잘못된 입력값입니다.", "001"),
    NOT_FOUND_ACCOUNT(404, "계정을 찾을 수 없습니다.", "002"),
    NOT_FOUND_REVENUE(400, "정산금을 찾을 수 없습니다.", "003"),
    NOT_FOUND_TOPBOARD(400, "상위 게시물을 찾을 수 없습니다.", "004"),
    IlLEGAL_PARAMETER(400, "잘못된 인자입니다.", "005"),
    UN_AUTHENTICATION(401, "인증되지 않았습니다.", "006"),
    FORBIDDEN(403, "잘못된 접근입니다.", "007"),
    NOT_FOUND_BOARD(404, "게시글을 찾을 수 없습니다.", "008"),
    RETRYABLE_EXCEPTION(500, "외부 연결에 실패 했습니다.", "009"),
    FAIL_BATCH(500, "배치작업에 실패 했습니다.", "010"),
    ;

    private int status;

    private String message;

    private String code;

    ExceptionCode(int status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
