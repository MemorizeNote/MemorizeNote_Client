package com.asb.memorizenote.widget.libraryseat;

/**
 * Created by azureskybox on 15. 10. 27.
 */
import android.content.Context;

import com.asb.memorizenote.utils.MNLog;

import net.htmlparser.jericho.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibrarySeatParser {

    OnLibrarySeatsParsedListener mListener;

    public LibrarySeatParser() {

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

                Source source = new Source(new URL("http://222.233.169.126/EZ5500/SEAT/RoomStatus.aspx"));
                List<Element> totalElementList = source.getAllElements();
                List<Element> tempElementList = null;
                List<Element> seatInfoElementList = null;

                //Find root TABLE element
                for(Element element : totalElementList) {
                    if(element.getStartTag().getName().equals(HTMLElementName.TABLE)) {
                        tempElementList = element.getChildElements();
                        break;
                    }
                }

                if(tempElementList == null)
                    return;

                try {
                    tempElementList = tempElementList.get(1).getChildElements().get(1).getChildElements();
                }
                catch(Exception e) {
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

                Pattern pattern = Pattern.compile("[0-9]*");
                try {
                    Matcher match;

                    String notebookRoomTotal = seatInfoElementList.get(6).getChildElements().get(1).getContent().getTextExtractor().toString();
                    String noteBookRoomUsing = seatInfoElementList.get(6).getChildElements().get(2).getContent().getTextExtractor().toString();
                    String noteBookRoomRemaining = seatInfoElementList.get(6).getChildElements().get(3).getContent().getTextExtractor().toString();
                    String noteBookRoomReserved = seatInfoElementList.get(6).getChildElements().get(5).getContent().getTextExtractor().toString();
                    String noteBookRoomNextEntering = seatInfoElementList.get(6).getChildElements().get(7).getContent().getTextExtractor().toString();

                    MNLog.d(notebookRoomTotal+","+noteBookRoomUsing+","+noteBookRoomRemaining+","+noteBookRoomReserved+","+noteBookRoomNextEntering);

                    mListener.onParsed(notebookRoomTotal, noteBookRoomUsing, noteBookRoomRemaining, noteBookRoomReserved, noteBookRoomNextEntering);
                }
                catch(Exception e) {
                    MNLog.d(e.toString());
                }

            } catch (IOException e) {
                MNLog.d(e.toString());
            }
        }
    }

    public interface OnLibrarySeatsParsedListener {
        void onParsed(String total, String current, String remain, String reserved, String next);
    }
}
