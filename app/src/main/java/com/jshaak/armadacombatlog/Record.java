package com.jshaak.armadacombatlog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jordan Shaak on 9/12/2017.
 */

public class Record implements Parcelable {

    private String date, opponent, playerFaction, opFor, tourneyPoints, objective;
    private boolean wentFirst, fleetWipe;

    public Record(String date, String opponent, String playerFaction, String opFor, String tourneyPoints,
                  boolean wentFirst, String objective, boolean fleetWipe) {
        this.date = date;
        this.opponent = opponent;
        this.playerFaction = playerFaction;
        this.opFor = opFor;
        this.tourneyPoints = tourneyPoints;
        this.wentFirst = wentFirst;
        this.objective = objective;
        this.fleetWipe = fleetWipe;
    }

    protected Record(Parcel in) {
        date = in.readString();
        opponent = in.readString();
        playerFaction = in.readString();
        opFor = in.readString();
        tourneyPoints = in.readString();
        objective = in.readString();
        wentFirst = in.readByte() != 0;
        fleetWipe = in.readByte() != 0;
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    public String toString() {
        String turnOrder, endCon;
        if(wentFirst)
        {
            turnOrder = "first player";
        }
        else
        {
            turnOrder = "second player";
        }
        if(fleetWipe)
        {
            endCon = "extermination";
        }
        else
        {
            endCon = "round limit";
        }

        return date + " - " + playerFaction + " vs " + opFor + " (" + opponent + ")\nScore: " + tourneyPoints + " as " + turnOrder
                + "\nObj: " + objective + " (" + endCon + ")";
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getOpponent() {
        return opponent;
    }
    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getPlayerFaction() {
        return playerFaction;
    }
    public void setPlayerFaction(String playerFaction) {
        this.playerFaction = playerFaction;
    }

    public String getOpFor() {
        return opFor;
    }
    public void setOpFor(String opFor) {
        this.opFor = opFor;
    }

    public String getTourneyPoints() {
        return tourneyPoints;
    }
    public void setTourneyPoints(String tourneyPoints) {
        this.tourneyPoints = tourneyPoints;
    }

    public boolean isWentFirst() {
        return wentFirst;
    }
    public void setWentFirst(boolean wentFirst) {
        this.wentFirst = wentFirst;
    }

    public String getObjective() {
        return objective;
    }
    public void setObjective(String objective) {
        this.objective = objective;
    }

    public boolean isFleetWipe() {
        return fleetWipe;
    }
    public void setFleetWipe(boolean fleetWipe) {
        this.fleetWipe = fleetWipe;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(opponent);
        parcel.writeString(playerFaction);
        parcel.writeString(opFor);
        parcel.writeString(tourneyPoints);
        parcel.writeString(objective);
        parcel.writeByte((byte) (wentFirst ? 1 : 0));
        parcel.writeByte((byte) (fleetWipe ? 1 : 0));
    }
}
