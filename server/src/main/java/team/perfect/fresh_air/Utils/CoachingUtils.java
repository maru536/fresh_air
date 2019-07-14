package team.perfect.fresh_air.Utils;

import team.perfect.fresh_air.Contract.DustStandardContract;
import team.perfect.fresh_air.DAO.PublicDust;
import team.perfect.fresh_air.DAO.Dust;

public class CoachingUtils {
    private static final int PM25_DIFF_THRESHOLD = 50;
    private static final int PM100_DIFF_THRESHOLD = 50;
    
    public static String makeLatestDustCoachingMessage(Dust dust) {
        String message;

        if (dust.getPm100() < DustStandardContract.goodPm100)
            message = "미세먼지가 좋아요! 산책하시는건 어떤가요?\n";
        else if (dust.getPm100() < DustStandardContract.normalPm100)
            message = "미세먼지가 보통이네요. 일상을 즐겨주세요.\n";
        else if (dust.getPm100() < DustStandardContract.badPm100)
            message = "미세먼지가 나빠요... 마스크를 착용하세요.\n";
        else
            message = "미세먼지가 매우 나빠요. 외출을 자제해주세요!\n";

        if (dust.getPm25() < DustStandardContract.goodPm25)
            message += "초미세먼지가 좋아요! 산책하시는건 어떤가요?";
        else if (dust.getPm25() < DustStandardContract.normalPm25)
            message += "초미세먼지가 보통이네요. 일상을 즐겨주세요.";
        else if (dust.getPm25() < DustStandardContract.badPm25)
            message += "초미세먼지가 나빠요... 마스크를 착용하세요.";
        else
            message += "초미세먼지가 매우 나빠요. 외출을 자제해주세요!";

        return message;
    }

    public static String makeDiffCoachingMessage(Dust dust, PublicDust publicDust) {
        String message = "";
        int diffPm100 = dust.getPm100() - publicDust.getPm100();
        int diffPm25 = dust.getPm25() - publicDust.getPm25();

        if (publicDust.getPm100() > 0 && Math.abs(diffPm100) > PM100_DIFF_THRESHOLD) {
            if (diffPm100 > 0) {
                message += "여기는 주변보다 미세먼지 농도가 높아요.\n";
            } else {
                message += "여기는 주변보다 미세먼지 농도가 낮아요.\n";
            }
        }

        if (publicDust.getPm25() > 0 && Math.abs(diffPm25) > PM25_DIFF_THRESHOLD) {
            if (diffPm25 > 0) {
                message += "여기는 주변보다 초미세먼지 농도가 높아요.\n";
            } else {
                message += "여기는 주변보다 초미세먼지 농도가 낮아요.\n";
            }
        }

        return message;
    }
}