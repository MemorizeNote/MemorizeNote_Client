package com.asb.memorizenote.widget.libraryseat;

/**
 * Created by azureskybox on 15. 10. 27.
 */

import com.asb.memorizenote.utils.MNLog;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibrarySeatParser {

    public static final int LIB_TYPE_PYEONGCHON = 0;
    public static final int LIB_TYPE_SANBON = 1;
    public static final int LIB_TYPE_JOONGANG = 2;

    private int mLibrary = 0;

    private static final String LIB_SEAT_URL_PYOENGCHON = "http://222.233.169.126/EZ5500/SEAT/RoomStatus.aspx";
    private static final String LIB_SEAT_URL_SANBON = "http://210.99.84.115:8800/roomstatus.aspx";
    private static final String LIB_SEAT_URL_JOONGANG = "http://211.57.49.199/EZ5500/RoomStatus/room_status.asp";

    private static String[] LIB_SEAT_URL_ARRAY = {
            "http://222.233.169.126/EZ5500/SEAT/RoomStatus.aspx",
            "http://210.99.84.115:8800/roomstatus.aspx",
            "http://211.57.49.199/EZ5500/RoomStatus/room_status.asp"
    };

    OnLibrarySeatsParsedListener mListener;

    public LibrarySeatParser(int libraryType) {
        mLibrary = libraryType;
    }

    public void startParsing(OnLibrarySeatsParsedListener listener) {
        mListener = listener;

        Thread t = new Thread(new ParsingRunnable());
        t.start();
    }

    private class ParsingRunnable implements Runnable {

        @Override
        public void run() {
            try {
                boolean checkOneMore = true;
                Source source = new Source(new URL(LIB_SEAT_URL_ARRAY[mLibrary]));
                List<Element> totalElementList = source.getAllElements();
                List<Element> tempElementList = null;
                List<Element> seatInfoElementList = null;

                //Find root TABLE element
                for(Element element : totalElementList) {
                    if(element.getStartTag().getName().equals(HTMLElementName.TABLE)) {
                        if(mLibrary == LIB_TYPE_JOONGANG && checkOneMore) {
                            checkOneMore = false;
                            continue;
                        }

                        tempElementList = element.getChildElements();
                        break;
                    }
                }

                if(tempElementList == null) {
                    MNLog.d("tempElementList null");
                    return;
                }

                try {
                    tempElementList = tempElementList.get(mLibrary == LIB_TYPE_JOONGANG?0:1).getChildElements().get(1).getChildElements();
                }
                catch(Exception e) {
                    MNLog.d(e.toString());
                    return;
                }

                for(Element element : tempElementList) {
                    if(element.getStartTag().getName().equals(HTMLElementName.TABLE)) {
                        seatInfoElementList = element.getChildElements();
                        break;
                    }
                }

                if(seatInfoElementList == null)
                    return;

                String notebookRoomTotal = "";
                String noteBookRoomUsing = "";
                String noteBookRoomRemaining = "";
                String noteBookRoomReserved = "";
                String noteBookRoomNextEntering = "";
                switch(mLibrary) {
                    case LIB_TYPE_PYEONGCHON:
                        notebookRoomTotal = seatInfoElementList.get(6).getChildElements().get(1).getContent().getTextExtractor().toString();
                        noteBookRoomUsing = seatInfoElementList.get(6).getChildElements().get(2).getContent().getTextExtractor().toString();
                        noteBookRoomRemaining = seatInfoElementList.get(6).getChildElements().get(3).getContent().getTextExtractor().toString();
                        noteBookRoomReserved = seatInfoElementList.get(6).getChildElements().get(5).getContent().getTextExtractor().toString();
                        noteBookRoomNextEntering = seatInfoElementList.get(6).getChildElements().get(7).getContent().getTextExtractor().toString();
                        break;
                    case LIB_TYPE_SANBON:
                        notebookRoomTotal = seatInfoElementList.get(3).getChildElements().get(1).getContent().getTextExtractor().toString();
                        noteBookRoomUsing = seatInfoElementList.get(3).getChildElements().get(2).getContent().getTextExtractor().toString();
                        noteBookRoomRemaining = seatInfoElementList.get(3).getChildElements().get(3).getContent().getTextExtractor().toString();
                        noteBookRoomReserved = seatInfoElementList.get(3).getChildElements().get(5).getContent().getTextExtractor().toString();
                        noteBookRoomNextEntering = seatInfoElementList.get(3).getChildElements().get(7).getContent().getTextExtractor().toString();
                        break;
                    case LIB_TYPE_JOONGANG:
                        notebookRoomTotal = seatInfoElementList.get(3).getChildElements().get(3).getContent().getTextExtractor().toString();
                        noteBookRoomUsing = seatInfoElementList.get(3).getChildElements().get(2).getContent().getTextExtractor().toString();
                        noteBookRoomRemaining = seatInfoElementList.get(3).getChildElements().get(1).getContent().getTextExtractor().toString();
                        noteBookRoomReserved = seatInfoElementList.get(3).getChildElements().get(5).getContent().getTextExtractor().toString();
                        noteBookRoomNextEntering = seatInfoElementList.get(3).getChildElements().get(6).getContent().getTextExtractor().toString();
                        break;
                }

                MNLog.d(notebookRoomTotal+","+noteBookRoomUsing+","+noteBookRoomRemaining+","+noteBookRoomReserved+","+noteBookRoomNextEntering);

                mListener.onParsed(notebookRoomTotal, noteBookRoomUsing, noteBookRoomRemaining, noteBookRoomReserved, noteBookRoomNextEntering);

            } catch (IOException e) {
                MNLog.d(e.toString());
            }
        }
    }

    public interface OnLibrarySeatsParsedListener {
        void onParsed(String total, String current, String remain, String reserved, String next);
    }
}
