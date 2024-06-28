package com.tscprinting;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;
import com.example.tscdll.TscWifiActivity;
import com.facebook.react.bridge.Promise;
import android.util.Log;
import java.util.Arrays;
import java.util.Locale;

public class TscPrintingModule extends ReactContextBaseJavaModule {
        private final ReactApplicationContext reactContext;
        static Double heightRatio = 1.0;
        static Double widthRatio = 1.0;
        static Double numLabel = 4.0; 
        static Boolean _isBookstore = false;

        public TscPrintingModule(ReactApplicationContext reactContext) {
                super(reactContext);
                this.reactContext = reactContext;
        }



        @Override
        public String getName() {
                return "TscPrinting";
        }

        public void prepareLabel(TscWifiActivity instance, ReadableMap config) throws Exception {
                String ip = config.getString("ip");
                heightRatio = config.getDouble("heightRatio");
                widthRatio = config.getDouble("widthRatio");
                Integer height = (int) (heightRatio * 150);
                Integer width = (int) (widthRatio * 100);
                instance.openport(ip, 9100);
                instance.setup(width, height, 4, 10, 0, 3, 0);
                instance.sendcommand("CLS\n");
                instance.sendcommand("CODEPAGE UTF-8\n");
                instance.sendcommand("DIRECTION 1\n");
        }

        @ReactMethod
        public void printTote(ReadableMap config, ReadableMap data, Promise promise) {
                final TscWifiActivity instance = new TscWifiActivity();
                final ReadableMap config2 = config;
                final ReadableMap data2 = data;
                final Promise promise2 = promise;
                new Thread(new Runnable() {
                        public void run() {
                                try {
                                        int a = data2.getInt("fromId");
                                        int b = data2.getInt("toId");
                                        String size = data2.getString("size");
                                        prepareLabel(instance, config2);
                                        initializeTote(instance, a, b, size);
                                        promise2.resolve("");
                                } catch (Exception e) {
                                        promise2.reject(e);
                                }
                                instance.sendcommand("CLS\n");
                                instance.closeport(5000);
                        }
                }).start();

        }

        public void initializeTote(TscWifiActivity instance, int a, int b, String size) throws Exception {
                if (heightRatio < 1) {
                        numLabel = 3.0;
                }
                String[] block = { "", "", "", "" };
                int count = 0;
                for (int i = a; i <= b; i++) {
                        block[count] = size + "-" + Integer.toString(i);
                        if (count == numLabel - 1) {
                                proceedPrintMultiLabel(instance, block, false);
                                count = 0;
                                Arrays.fill(block, "");
                        } else {
                                count++;
                        }
                        if (i == b && count != 0) {
                                proceedPrintMultiLabel(instance, block, false);
                        }
                }
        }

        public void proceedPrintMultiLabel(TscWifiActivity instance, String[] block, Boolean hasArrow)
                        throws Exception {
                String content = "";
                instance.sendcommand("CLS\n");
                int x = 22, y = 280, text = 80, qrcode = 50;
                Double wRatio = widthRatio;
                Double hRatio = (4 / numLabel) * heightRatio;

                for (int i = 0; i < numLabel; i++) {
                        if (block[i] != "") {

                                instance.sendcommand(String.format(Locale.US, "BOX %f,%f,%f,%f,12,10\n", 12 * wRatio,
                                                x * hRatio, 790 * wRatio, y * hRatio));
                                if (hasArrow&&!_isBookstore) {
                                        instance.sendcommand(String.format(Locale.US, "PUTBMP %f,%f,\"%s\"\n",
                                                        30 * wRatio, text * hRatio - 20, "arrowup.bmp"));
                                }

                                instance.sendcommand(String.format(Locale.US,
                                                "BLOCK %f,%f,%f,%f,\"TAHOMAB.TTF\",0,70,70,5,2,1,\"%s\"\n ",
                                                120 * wRatio, text * hRatio + 15, 390 * wRatio, 105 * hRatio,
                                                block[i]));

                                instance.sendcommand(String.format(Locale.US, "QRCODE %f,%f,H,%f,M,0,M2, \"S%s\"\n",
                                                555 * wRatio, qrcode * hRatio, 10 * hRatio, block[i]));
                                x += 305 * wRatio;
                                y += 305 * wRatio;
                                text += 305 * wRatio;
                                qrcode += 305 * wRatio;
                        }
                }
                instance.sendcommand("PRINT 1,1\n");
        }

        @ReactMethod
        public void printShelf(ReadableMap config, ReadableMap data,Boolean isBookstore, Promise promise) {
                _isBookstore = isBookstore;
                final TscWifiActivity instance = new TscWifiActivity();
                final ReadableMap config2 = config;
                final ReadableMap data2 = data;
                final Promise promise2 = promise;
                new Thread(new Runnable() {
                        public void run() {
                                try {
                                        prepareLabel(instance, config2);
                                        initializeShelf(instance, data2);
                                        promise2.resolve("success2");

                                } catch (Exception e) {
                                        promise2.reject(e);
                                }
                                instance.sendcommand("CLS\n");
                                instance.closeport(5000);
                        }
                }).start();
        }

        @ReactMethod
        public void printSampleHC(ReadableMap config, ReadableMap data, Promise promise) {
                
                final TscWifiActivity instance = new TscWifiActivity();
                final ReadableMap config2 = config;
                final ReadableMap data2 = data;
                final Promise promise2 = promise;
                new Thread(new Runnable() {
                        public void run() {
                                try {
                                        prepareLabel(instance, config2);

                                        instance.sendcommand(String.format(Locale.US, "BOX %f,%f,%f,%f,8,10\n", 42 * widthRatio,
                                        32 * heightRatio, 790 * widthRatio, 1185 * heightRatio));
                                        
                                        instance.sendcommand(String.format(Locale.US, "BARCODE %f,%f,\"128\",%f,2,90,6,2,\"%s\"\n", 700 * widthRatio,
                                        100 * heightRatio, 400 * heightRatio, data2.getString("data")));
                                        instance.sendcommand("PRINT 1,1\n");
                                        promise2.resolve("success2");

                                } catch (Exception e) {
                                        promise2.reject(e);
                                }
                                instance.sendcommand("CLS\n");
                                instance.closeport(5000);
                        }
                }).start();
        }

        public void initializeShelf(TscWifiActivity instance, ReadableMap data) throws Exception {
                if (heightRatio < 1) {
                        numLabel = 3.0;
                }
                int floor = data.getInt("floor");
                int colFrom = data.getInt("colFrom");
                int colTo = data.getInt("colTo");
                String block = data.getString("block");
                int count = 0;
                String[] label = { "", "", "", "" };
                ReadableArray rows = data.getArray("rows");
                for (int i = colFrom; i <= colTo; i++) {
                        for (int j = 0; j < rows.size(); j++) {
                                label[count] = block + "-" + Integer.toString(i) + "-" + rows.getInt(j);
                                if (floor != 0) {
                                        label[count] = "L" + Integer.toString(floor) + "-" + label[count];
                                }
                                if (count == numLabel - 1) {
                                        proceedPrintMultiLabel(instance, label, true);
                                        count = 0;
                                        Arrays.fill(label, "");
                                } else {
                                        count++;
                                }
                        }
                }
                if (count != 0) {
                        proceedPrintMultiLabel(instance, label, true);
                }
        }

        @ReactMethod
        public void printLabel(ReadableMap config, ReadableMap data, Promise promise) {

                final TscWifiActivity instance = new TscWifiActivity();
                final ReadableMap config2 = config;
                final ReadableMap data2 = data;
                final Promise promise2 = promise;
                new Thread(new Runnable() {
                        public void run() {
                                try {
                                        prepareLabel(instance, config2);
                                        printLayoutLabel(instance, data2);
                                        printContentLabel(instance, data2);
                                        instance.sendcommand("PRINT 1,1\n");
                                        promise2.resolve("");
                                } catch (Exception e) {
                                        promise2.reject(e);
                                }
                                instance.sendcommand("CLS\n");
                                instance.closeport(5000);
                        }
                }).start();
        }

        public void printContentLabel(TscWifiActivity instance, ReadableMap a) throws Exception {
        
                String partnerCode = a.getString("partnerCode");
                String partnerName = a.getString("partnerName");

                String content = "- Sách, văn hoá phẩm và văn phòng phẩm";
                String noteTitle = "Nội dung hàng hoá:";
                Integer noteWidth =( partnerName != null && partnerCode != null? 238:310);
                String specialDate = a.getString("sDate");
                Integer fontSizeNote= 10;
                String fontContent = "TAHOMA.TTF";
                String requireVAT = a.getString("requireVAT");

                // Block VAT
                if(requireVAT.equals("1")){        

                        instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"ĐÃ NHẬN ĐỦ HOÁ ĐƠN VAT\" \n ", 
                                        60 * widthRatio, 695 * heightRatio, 12 * heightRatio, 12 * heightRatio));

                }

                Boolean useBarcode = false ;
                try {
                        useBarcode = a.getBoolean("useBarcode");
                        
                } catch (Exception e) {
                        useBarcode = false;
                } finally {
                        if(useBarcode){
                                instance.sendcommand(String.format(Locale.US, "BARCODE %f,%f,\"128\",%f,2,0,2,2,\"%s\"\n", 60 * widthRatio,
                                50 * heightRatio, 250 * heightRatio, a.getString("deliveryId")));
                        } else {
                                instance.sendcommand(String.format(Locale.US, "QRCODE %f,%f,H,%f,M,0,M2 ,\"S%s\"\n", 76 * widthRatio,
                                66 * heightRatio, 10 * widthRatio, a.getString("deliveryId")));
                        }
                }
               

                instance.sendcommand(String.format(Locale.US, "QRCODE %f,%f,H,%f,M,0,M2 ,\"S%s\"\n", 504 * widthRatio,
                                853 * heightRatio, 10 * widthRatio, a.getString("orderId").replace("_", "")));

                instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMA.TTF\",0,%f,%f,1,\"%s\" \n ",
                                400 * widthRatio, 135 * heightRatio, 10 * heightRatio, 10 * heightRatio,
                                a.getString("date")));

                instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"%s\" \n ",
                                400 * widthRatio, 175 * heightRatio, 10 * heightRatio, 10 * heightRatio,
                                a.getString("orderId")));

                instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMA.TTF\",0,%f,%f,1,\"%s\" \n ",
                                400 * widthRatio, 215 * heightRatio, 25 * heightRatio, 25 * heightRatio,
                                a.getString("deliveryPartner")));
                if(partnerName != null && partnerCode != null){
                        content = "- Note: Nhà bán hàng FAHASA trên " + partnerName;
                        noteTitle = "Ghi chú giao hàng:";
                        instance.sendcommand(String.format(Locale.US,
                                        "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Mã đơn " + partnerName + ": \" \n ", 400 * widthRatio,
                                        295 * heightRatio, 10 * heightRatio, 10 * heightRatio));

                        instance.sendcommand(String.format(Locale.US,
                                        "BLOCK %f,%f,%f,%f,\"TAHOMA.TTF\",0,%f,%f,%f,0,1,\"%s\" \n ", 580 * widthRatio,
                                        295 * heightRatio, 310 * widthRatio, 60 * heightRatio, 9 * widthRatio,
                                        9 * widthRatio, 5 * widthRatio, partnerCode));
                } else if (specialDate!=null){
                        noteTitle = "Ghi chú giao hàng:";
                        content = "16h00 - 21h00 ngày " + specialDate;
                }

                try {
                        if(a.getString("eventDeliveryDate")!= null){
                                content = " Hẹn ngày giao:  " + a.getString("eventDeliveryDate");
                                noteTitle = "";    
                                fontSizeNote = 15;
                                fontContent = "TAHOMAB.TTF";

                        }
                } catch (Exception e) {
                        //TODO: handle exception
                }



                // instance.sendcommand(String.format(Locale.US,
                //                 "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Người nhận:   \" \n ", 330 * widthRatio,
                //                 255 * heightRatio, 10 * heightRatio, 10 * heightRatio));

                instance.sendcommand(String.format(Locale.US,
                                "BLOCK %f,%f,%f,%f,\"TAHOMA.TTF\",0,%f,%f,%f,0,1,\"%s\" \n ", 60 * widthRatio,
                                390 * heightRatio, 255 * widthRatio, 50 * heightRatio, 11 * heightRatio,
                                11 * heightRatio, 5 * widthRatio, a.getString("shippingName")));                                

                // instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"SĐT:    \" \n ",
                //                 330 * widthRatio, 295 * heightRatio, 11 * heightRatio, 11 * heightRatio));

                instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMA.TTF\",0,%f,%f,1,\"%s\" \n ",
                                350 * widthRatio, 390 * heightRatio, 11 * heightRatio, 11 * heightRatio,
                                a.getString("shippingPhone")));

                instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Địa chỉ:\" \n ",
                                60 * widthRatio, 430 * heightRatio, 10 * heightRatio, 10 * heightRatio));


                instance.sendcommand(String.format(Locale.US,
                                "BLOCK %f,%f,%f,%f,\"TAHOMA.TTF\",0,%f,%f,%f,0,1,\"%s\"\n ", 60 * widthRatio,
                                470 * heightRatio, 400 * widthRatio, 180 * heightRatio, 10 * widthRatio,
                                10 * widthRatio, 5 * widthRatio, a.getString("shippingStreet")));

                instance.sendcommand(String.format(Locale.US,
                                "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Huyện/ Xã: \" \n ", 520 * widthRatio,
                                390 * heightRatio, 11 * heightRatio, 11 * heightRatio));

                instance.sendcommand(String.format(Locale.US,
                                "BLOCK %f,%f,%f,%f,\"TAHOMA.TTF\",0,%f,%f,%f,0,1,\"%s\" \n ", 520 * widthRatio,
                                420 * heightRatio, 380 * widthRatio, 60 * heightRatio, 9 * widthRatio, 9 * widthRatio,
                                5 * widthRatio, a.getString("ward")));


                instance.sendcommand(String.format(Locale.US,
                                "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Quận/Huyện: \" \n ", 520 * widthRatio,
                                515 * heightRatio, 11 * heightRatio, 11 * heightRatio));

                instance.sendcommand(String.format(Locale.US,
                                "BLOCK %f,%f,%f,%f,\"TAHOMA.TTF\",0,%f,%f,%f,0,1,\"%s\" \n ", 520 * widthRatio,
                                545 * heightRatio, 380 * widthRatio, 60 * heightRatio, 9 * widthRatio, 9 * widthRatio,
                                5 * widthRatio, a.getString("district")));


                instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Tỉnh/TP: \" \n ",
                                520 * widthRatio, 645 * heightRatio, 11 * heightRatio, 11 * heightRatio));

                instance.sendcommand(String.format(Locale.US,
                                "BLOCK %f,%f,%f,%f,\"TAHOMA.TTF\",0,%f,%f,%f,0,1,\"%s\" \n ", 520 * widthRatio,
                                675 * heightRatio, 380 * widthRatio, 60 * heightRatio, 9 * widthRatio, 9 * widthRatio,
                                5 * widthRatio, a.getString("province")));
                

                instance.sendcommand(String.format(Locale.US,
                                "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"ĐƯỢC XEM HÀNG\" \n ", 520 * widthRatio,
                                800 * heightRatio, 12 * heightRatio, 12 * heightRatio));

                instance.sendcommand(String.format(Locale.US,
                                "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Tiền thu hộ: \" \n ", 60 * widthRatio,
                                645 * heightRatio, 11 * heightRatio, 11 * heightRatio));

                instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMA.TTF\",0,%f,%f,1,\"%s\" \n ",
                                230 * widthRatio, 645 * heightRatio, 12 * heightRatio, 12 * heightRatio,
                                a.getString("total")));

                instance.sendcommand(String.format(Locale.US, "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Ghi chú: \" \n ",
                                60 * widthRatio, 800 * heightRatio, 11 * heightRatio, 11 * heightRatio));

                instance.sendcommand(String.format(Locale.US,
                                "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\""+noteTitle+" \" \n ", 60 * widthRatio,
                                950 * heightRatio, 10 * heightRatio, 10 * heightRatio));


                instance.sendcommand(String.format(Locale.US,
                                "BLOCK %f,%f,%f,%f,\"TAHOMA.TTF\",0,%f,%f,%f,0,1,\"%s\"\n ", 170 * widthRatio,
                                800 * heightRatio, noteWidth * widthRatio, 120 * heightRatio, 10 * widthRatio,
                                10 * widthRatio, 5 * widthRatio, a.getString("note")));

                // instance.sendcommand(String.format(Locale.US,
                //                 "BLOCK %f,%f,%f,%f,\"TAHOMA.TTF\",0,%f,%f,%f,0,1,\"%s\"\n ", 220 * widthRatio,
                //                 800 * heightRatio, noteWidth * widthRatio, 120 * heightRatio, 10 * widthRatio,
                //                 10 * widthRatio, 5 * widthRatio, "flksdjflkj lksdjf lsdjfl jsdlf jsdlfjlksd jfald fjasdjf lasdhf lksadhf klsadhflkasd hfklasdh kflsdahfk sadlf jlhfdla ad hdf"));

                instance.sendcommand(String.format(Locale.US,
                                "BLOCK %f,%f,%f,%f,\"%s\",0,%f,%f,%f,0,1,\"%s\"\n ", 60 * widthRatio,
                                1000 * heightRatio, 450 * widthRatio, 200 * heightRatio,fontContent, fontSizeNote * widthRatio,
                                fontSizeNote * widthRatio, 5 * widthRatio, content));

                String dPartner = a.getString("deliveryPartner");

                instance.sendcommand(String.format(Locale.US, "PUTBMP %f,%f,\"kerry.bmp\"\n", 300 * widthRatio,
                                1050 * heightRatio));

                String command = "";

                String fHeader = widthRatio >= 1 ? "fhbig.bmp" : "fhsmall.bmp";
                String fFooter = widthRatio >= 1 ? "ffbig.bmp" : "ffsmall.bmp";
                String speedlink = widthRatio >= 1 ? "splbig.bmp" : "splsmall.bmp";
                String ninja = widthRatio >= 1 ? "ninjabig.bmp" : "ninjasmall.bmp";
                String kerry = widthRatio >= 1 ? "kerrybig.bmp" : "kerrysmall.bmp";

                instance.sendcommand(String.format(Locale.US, "PUTBMP %f,%f,\"%s\"\n", 400 * widthRatio,
                                55 * heightRatio, fHeader));

                Boolean hasLogo = a.getBoolean("hasLogo");
                if (hasLogo) {
                        switch (dPartner) {
                        case "Ninja Van":
                        case "Ninja Van_SD":
                                command = String.format(Locale.US, "PUTBMP %f,%f,\"%s\"\n", 240 * widthRatio,
                                                1020 * heightRatio, ninja);
                                break;
                        case "Kerry":
                        case "Kerry_SD":
                                command = String.format(Locale.US, "PUTBMP %f,%f,\"%s\"\n", 240 * widthRatio,
                                                1050 * heightRatio, kerry);
                                break;
                        case "Speedlink":
                        case "Speedlink_SD":
                                command = String.format(Locale.US, "PUTBMP %f,%f,\"%s\"\n", 240 * widthRatio,
                                                1100 * heightRatio, speedlink);
                                break;
                        case "Fahasa":
                        case "Fahasa_SD":
                                command = String.format(Locale.US, "PUTBMP %f,%f,\"%s\"\n", 240 * widthRatio,
                                                1080 * heightRatio, fFooter);
                                break;
                        default:
                                command = String.format(Locale.US,
                                                "BLOCK %f,%f,%f,%f,\"TAHOMAB.TTF\",0,%f,%f,%f,0,1,\"%s\"\n ",
                                                280 * widthRatio, 1095 * heightRatio, 250 * widthRatio,
                                                60 * heightRatio, 15 * widthRatio, 15 * widthRatio, 5 * widthRatio,
                                                dPartner);
                        }
                        instance.sendcommand(String.format(Locale.US,
                                        "TEXT %f,%f,\"TAHOMAB.TTF\",0,%f,%f,1,\"Được giao bởi: \" \n ", 60 * widthRatio,
                                        1100 * heightRatio, 10 * heightRatio, 10 * heightRatio));
                        instance.sendcommand(command);
                }
        }

        public void printLayoutLabel(TscWifiActivity instance, ReadableMap a) throws Exception {
                instance.sendcommand(String.format(Locale.US, "BOX %f,%f,%f,%f,8,10\n", 42 * widthRatio,
                                32 * heightRatio, 790 * widthRatio, 1185 * heightRatio));

                instance.sendcommand(String.format(Locale.US, "DIAGONAL %f,%f,%f,%f,4\n", 42 * widthRatio,
                                375 * heightRatio, 790 * widthRatio, 375 * heightRatio));
                instance.sendcommand(String.format(Locale.US, "DIAGONAL %f,%f,%f,%f,4\n", 485 * widthRatio,
                                490.5 * heightRatio, 790 * widthRatio, 490.5 * heightRatio));
                instance.sendcommand(String.format(Locale.US, "DIAGONAL %f,%f,%f,%f,4\n", 42 * widthRatio,
                                626 * heightRatio, 790 * widthRatio, 626 * heightRatio));
                instance.sendcommand(String.format(Locale.US, "DIAGONAL %f,%f,%f,%f,4\n", 485 * widthRatio,
                                375 * heightRatio, 485 * widthRatio, 1185 * heightRatio));
                instance.sendcommand(String.format(Locale.US, "DIAGONAL %f,%f,%f,%f,4\n", 42 * widthRatio,
                                930 * heightRatio, 485 * widthRatio, 930 * heightRatio));
                instance.sendcommand(String.format(Locale.US, "DIAGONAL %f,%f,%f,%f,4\n", 42 * widthRatio,
                                761.5 * heightRatio, 790 * widthRatio, 761.5 * heightRatio));
        }

        @ReactMethod
        public void executePrint(ReadableMap config, ReadableArray listCmd, Promise promise) {
//                final TscWifiActivity instance = new TscWifiActivity();
                final tscOverrideClass instance = new tscOverrideClass();
                final ReadableMap config2 = config;
                final ReadableArray data2 = listCmd;
                final Promise promise2 = promise;
                new Thread(new Runnable() {
                        public void run() {
                                try {
                                        String ip = config2.getString("ip");
                                        Integer width = (int) config2.getDouble("width");
                                        Integer height = (int) config2.getDouble("height");
                                        instance.openport(ip, 9100);
                                        instance.setup(width, height, 4, 10, 0, 3, 0);
                                        instance.sendcommand("CLS\n");
                                        instance.sendcommand("CODEPAGE UTF-8\n");
                                        instance.sendcommand("DIRECTION 1\n");
                                        for(int i = 0; i < data2.size(); i++){
                                                String command = data2.getString(i);
                                                instance.sendcommand(command);
                                        }
                                        promise2.resolve("success2");

                                } catch (Exception e) {
                                        promise2.reject(e);
                                }
                                instance.sendcommand("CLS\n");
                                instance.closeport(5000);
                        }
                }).start();
        }
}
