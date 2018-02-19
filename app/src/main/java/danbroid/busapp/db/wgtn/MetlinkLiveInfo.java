package danbroid.busapp.db.wgtn;

import java.util.Date;

/**
 * Metlink departure information
 */
public class MetlinkLiveInfo {
  public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MetlinkLiveInfo.class);
  public static final String URL_ASSETS = "https://www.metlink.org.nz/assets";
  public static final String URL_STOP_DEPARTURES = "https://www.metlink.org" +
      ".nz/api/v1/StopDepartures";
  public static final String URL_METLINK = "https://www.metlink.org.nz";

  public Notice[] Notices = {};
  public Notice[] NoticesClosures = {};
  public Stop Stop;
  public Date LastModified;
  public ServiceInfo[] Services = {};

  public static String getDepartureInfoURL(String stopCode) {
    return URL_STOP_DEPARTURES + "/" + stopCode.substring(3);
  }

  @Override
  public String toString() {
    return "MetlinkLiveInfo[" + (Stop != null ? Stop.Name : "") + ":" + LastModified + "]";
  }

  public static class Notice {

    public String LineRef;


    public String LineNote;


    public String DirectionRef;


    public String MonitoringRef;


    public Date RecordedAtTime;

  }


  public static class Stop {
    public String Farezone;
    public String Sms;
    public String Name;
    public Date LastModified;
    public Double Long;
    public String Icon;
    public Double Lat;
  }

  public static class ServiceInfo {

    public Date AimedDeparture;
    public boolean IsRealTime;
    public String DestinationStopID;
    public String VehicleRef;
    public String OriginStopName;
    public String OperatorRef;
    public Date AimedArrival;
    public Date DisplayDeparture;
    public int DisplayDepartureSeconds;
    public String Direction;
    public Date ExpectedDeparture;
    public String DestinationStopName;
    public String ServiceID;
    public String DepartureStatus;
    public String OriginStopID;
    public String VehicleFeature;

    public Service Service;

    @Override
    public String toString() {
      return "Stop:" + OriginStopName + ":" + OriginStopID + ":Destination:" +
          DestinationStopName + ":" + Direction;
    }

    public String getTimetableURL() {
      return "https://www.metlink.org.nz" + Service.Link +
          (Direction.equals("Outbound") ? "/outbound" : "");
    }

    public static class Service {
      public String Code;
      public String Link;
      public String Mode;
      public String Name;
      public String TrimmedCode;
    }

  }
}
