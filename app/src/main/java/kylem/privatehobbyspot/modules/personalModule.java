package kylem.privatehobbyspot.modules;

import io.realm.annotations.RealmModule;
import kylem.privatehobbyspot.entities.DayOptions;
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;

/**
 * Created by kylem on 1/28/2018.
 */

@RealmModule(classes = {LocationPing.class, UserLocationPingViewOptions.class, DayOptions.class})
public class personalModule {
}
