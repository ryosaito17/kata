package kata.ex01;

import kata.ex01.model.HighwayDrive;
import kata.ex01.model.RouteType;
import kata.ex01.model.VehicleFamily;
import kata.ex01.util.HolidayUtils;

import java.time.LocalDateTime;

/**
 * @author kawasima
 */
public class DiscountServiceImpl implements DiscountService {

    private static final int MIDNIGHT_DISCOUNT_START_HOUR = 0;
    private static final int MIDNIGHT_DISCOUNT_END_HOUR = 4;
    private static final int BUSINESS_DAY_MORNING_DISCOUNT_START_HOUR = 6;
    private static final int BUSINESS_DAY_MORNING_DISCOUNT_END_HOUR = 9;
    private static final int BUSINESS_DAY_EVENING_DISCOUNT_START_HOUR = 17;
    private static final int BUSINESS_DAY_EVENING_DISCOUNT_END_HOUR = 20;

    @Override
    public long calc(HighwayDrive drive) {
        LocalDateTime enteredAt = drive.getEnteredAt();
        LocalDateTime exitedAt = drive.getExitedAt();
        RouteType routeType = drive.getRouteType();

        // 平日朝夕割引きかどうかをチェックする
        if (isBusinessDayDiscountTime(enteredAt, exitedAt) && RouteType.RURAL.equals(routeType)) {
            // 利用回数割引きをチェックする
            int countPerMonth = drive.getDriver().getCountPerMonth();
            if (countPerMonth >= 5 && countPerMonth <= 9) {
                return 30;
            } else if (countPerMonth >= 10) {
                return 50;
            }
        }

        // 休日割引きかどうかをチェックする
        if (HolidayUtils.isHoliday(enteredAt.toLocalDate()) && HolidayUtils.isHoliday(exitedAt.toLocalDate())
                && (VehicleFamily.STANDARD.equals(drive.getVehicleFamily()) || VehicleFamily.MINI.equals(drive.getVehicleFamily()))
                && RouteType.RURAL.equals(routeType)) {
            return 30;
        }

        // 深夜割引きかどうかをチェックする
        if (isMidnightDiscountTime(enteredAt, exitedAt)) {
            return 30;
        }

        return 0;
    }

    private boolean isBusinessDayDiscountTime(LocalDateTime enteredAt, LocalDateTime exitedAt) {

        if (HolidayUtils.isHoliday(enteredAt.toLocalDate()) || HolidayUtils.isHoliday(exitedAt.toLocalDate())) {
            return false;
        }

        // 朝割の基準日時
        LocalDateTime morningStartTm = LocalDateTime.of(enteredAt.getYear(), enteredAt.getMonth(), enteredAt.getDayOfMonth(), BUSINESS_DAY_MORNING_DISCOUNT_START_HOUR, 0);
        LocalDateTime morningEndTm = LocalDateTime.of(exitedAt.getYear(), exitedAt.getMonth(), exitedAt.getDayOfMonth(), BUSINESS_DAY_MORNING_DISCOUNT_END_HOUR, 0);
        // 夕方割の基準日時
        LocalDateTime eveningStartTm = LocalDateTime.of(enteredAt.getYear(), enteredAt.getMonth(), enteredAt.getDayOfMonth(), BUSINESS_DAY_EVENING_DISCOUNT_START_HOUR, 0);
        LocalDateTime eveningEndTm = LocalDateTime.of(exitedAt.getYear(), exitedAt.getMonth(), exitedAt.getDayOfMonth(), BUSINESS_DAY_EVENING_DISCOUNT_END_HOUR, 0);

        return isDiscountTime(morningStartTm, morningEndTm, enteredAt, exitedAt) || isDiscountTime(eveningStartTm, eveningEndTm, enteredAt, exitedAt);
    }

    private boolean isMidnightDiscountTime(LocalDateTime enteredAt, LocalDateTime exitedAt) {

        // 割引きの基準日時
        LocalDateTime startTm = LocalDateTime.of(enteredAt.getYear(), enteredAt.getMonth(), enteredAt.getDayOfMonth(), MIDNIGHT_DISCOUNT_START_HOUR, 0);
        LocalDateTime endTm = LocalDateTime.of(exitedAt.getYear(), exitedAt.getMonth(), exitedAt.getDayOfMonth(), MIDNIGHT_DISCOUNT_END_HOUR, 0);

        return isDiscountTime(startTm, endTm, enteredAt, exitedAt);
    }

    private boolean isDiscountTime(LocalDateTime ruleStartTm, LocalDateTime ruleEndTm, LocalDateTime targetStartTm, LocalDateTime targetEndTm) {
        return (targetStartTm.isBefore(ruleEndTm) || targetStartTm.isEqual(ruleEndTm))
                && (targetEndTm.isAfter(ruleStartTm) || (targetEndTm.isEqual(ruleStartTm)));
    }
}
