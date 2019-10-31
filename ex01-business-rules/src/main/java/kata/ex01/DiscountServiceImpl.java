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
        if (isMidnightDiscountTime(enteredAt.getHour(), exitedAt.getHour())) {
            return 30;
        }

        return 0;
    }

    // TODO: 日をまたぐ時の考慮必要
    private boolean isBusinessDayDiscountTime(LocalDateTime enteredAt, LocalDateTime exitedAt) {
        if (HolidayUtils.isHoliday(enteredAt.toLocalDate()) || HolidayUtils.isHoliday(exitedAt.toLocalDate())) {
            return false;
        }
        int enteredHour = enteredAt.getHour();
        int exitedHour = exitedAt.getHour();
        return (BUSINESS_DAY_MORNING_DISCOUNT_START_HOUR <= exitedHour && BUSINESS_DAY_MORNING_DISCOUNT_END_HOUR <= enteredHour)
                || (BUSINESS_DAY_EVENING_DISCOUNT_START_HOUR <= exitedHour && BUSINESS_DAY_EVENING_DISCOUNT_END_HOUR <= enteredHour);
    }

    // TODO: 日をまたぐ時の考慮必要
    private boolean isMidnightDiscountTime(int enteredHour, int exitedHour) {
        return MIDNIGHT_DISCOUNT_START_HOUR <= exitedHour && MIDNIGHT_DISCOUNT_END_HOUR <= enteredHour;
    }
}
