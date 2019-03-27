package social.reasoner.control.emulators;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import social.reasoner.model.Constants;
import social.reasoner.model.analisys.DataPoint;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by oscarr on 12/13/16.
 */
public class ExcelReaderWriter {
    private XSSFWorkbook workbook;
    private int rowOffset = 7;
    private int colOffset = 6;
    private int currentSheetIdx = 0;
    private String docName = "";
    private int maxNumRows = 0;
    private XSSFSheet excelSheet;
    private Map<String, Integer> map = new HashMap<>();


    public void openWorkbook(String fileName, boolean copyFile){
        fileName = "/Users/oscarr/Development/WEF/WEF-SocialReasoner/Execution/DavosResults/" + fileName.replace(".txt", ".xlsx");
        openExcelDoc(fileName, copyFile);
    }

    private void openExcelDoc(String fileName, boolean createExcel) {
        try{
            if( workbook == null || !docName.equals(fileName)) {
                File file = new File( fileName );
                if( createExcel ) {
                    copyFile(file);
                }
                FileInputStream inputStream = new FileInputStream(fileName);
                workbook = new XSSFWorkbook(inputStream);
                inputStream.close();
                SXSSFWorkbook wb = new SXSSFWorkbook(workbook);
                wb.setCompressTempFiles(true);
                docName = fileName;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<SystemIntentStep> readSheet(){
        Sheet sheet = workbook.getSheetAt( currentSheetIdx );
        Row row;
        List<SystemIntentStep>  intents = new ArrayList<>();
        if( !sheet.getSheetName().equals("Comparison") ) {
            boolean isFarewell = false;
            int rowPos = rowOffset;
            int lastRow = sheet.getLastRowNum();
            for ( ; rowPos < lastRow+1 && !isFarewell; rowPos++) {
                row = sheet.getRow(rowPos);
                if (row != null && row.getCell(0) != null ) {
                    SystemIntentStep intent = new SystemIntentStep(
                            row.getCell(0) == null? "" : row.getCell(0).getStringCellValue(), "",
                            row.getCell(1) == null? 0 : row.getCell(1).getNumericCellValue(),
                            row.getCell(2) == null? "" : row.getCell(2).getStringCellValue(),
                            row.getCell(3) == null? false :row.getCell(3).getStringCellValue().equals("SMILE") ? true : false,
                            row.getCell(4) == null? false :row.getCell(4).getStringCellValue().equals("GAZE_PARTNER") ? true : false,
                            colOffset > 5? row.getCell(4) == null? false : row.getCell(4).getStringCellValue().equals("AVAILABLE") : false);
                    intents.add(intent);
                    if( intent.getIntent().equals(Constants.FAREWELL) ){
                        isFarewell = true;
                    }
                }
            }
            if( rowPos > maxNumRows ){
                maxNumRows = rowPos;
            }
        }
        return intents;
    }


    public int getNumSheets(){
        if( workbook == null ){
            return 1;
        }
        return workbook.getNumberOfSheets();
    }

    public void setColOffset(int colOffset){
        this.colOffset = colOffset;
    }

    public void writeSheet(String results){
        try {
            String[] rows = results.split("\\n");
            Sheet sheet = workbook.getSheetAt(currentSheetIdx);
            Row row;
            int numRows = sheet.getLastRowNum();
            for (int rowPos = rowOffset; rowPos < numRows + 1 && (rowPos - rowOffset) < rows.length; rowPos++) {
                row = sheet.getRow(rowPos);
                if (row != null) {
                    String[] columns = rows[rowPos - rowOffset].split("\\t");
                    for (int colPos = colOffset; colPos < columns.length; colPos++) {
                        if (row.getCell(colPos) != null) {
                            row.getCell(colPos).setCellValue(columns[colPos]);
                        } else {
                            row.createCell(colPos).setCellValue(columns[colPos]);
                        }
                    }
                }
            }
            currentSheetIdx++;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeSheet(List<DataPoint> results){
        String result = computeStatistics(results);
        currentSheetIdx = 0;
        colOffset = 0;
        writeSheet(result);
    }

    private String computeStatistics(List<DataPoint> results){
        String result = "";
        String cs = results.get(0).getWozerOutput();
        for(int i = 0; i < results.size(); i++){
            DataPoint dataPoint = results.get(i);
            if( !cs.equals(dataPoint.getWozerOutput()) ){
                cs = dataPoint.getWozerOutput();
                result += countStatistics();
            }
            result += dataPoint.getRawData();
            String[] wozerOutput = dataPoint.getRawData().split("\\t");
            for(int j = 1; j < 27; j++){ // < wozerOutput.length
                if( j != 2) { //exclude rapport score
                    String key = j == 1? "intent_" + wozerOutput[j] : wozerOutput[j];
                    Integer value = map.get( key );
                    if (value == null) {
                        value = 0;
                    }
                    value++;
                    map.put( key, value);
                }
            }
            if( (i == results.size() - 1) ){
                result += countStatistics();
            }
        }
        return result;
    }

    private String countStatistics() {
        String statistics = "Total\t"; //id column
        for(String key : map.keySet() ){
            if( key.startsWith("intent_") ){
                statistics += "(" + key + ": " + map.get(key) + ") ";
            }
        }
        statistics = statistics.replace("intent_", "") + "\t"; // rapport score
        statistics += checkValue("HIGH_RAPPORT:MEDIUM_RAPPORT:LOW_RAPPORT");
        statistics += checkValue("RAPPORT_INCREASED:RAPPORT_DECREASED:RAPPORT_MAINTAINED");
        statistics += checkValue("SD:QESD:ASN:VSN:PR:ACK:RSE");
        statistics += checkValue("SMILE:NOT_SMILE");
        statistics += checkValue("GAZE_PARTNER:GAZE_ELSEWHERE");
        statistics += checkValue("AVAILABLE:NOT_AVAILABLE");
        statistics += checkValue("NUM_TURNS_LOWER_THAN_THRESHOLD:NUM_TURNS_HIGHER_THAN_THRESHOLD");
        statistics += checkValue("NOT_ASN_HISTORY_SYSTEM:ASN_HISTORY_SYSTEM");
        statistics += checkValue("NOT_VSN_HISTORY_SYSTEM:VSN_HISTORY_SYSTEM");
        statistics += checkValue("NOT_SD_HISTORY_SYSTEM:SD_HISTORY_SYSTEM");
        statistics += checkValue("NOT_QESD_HISTORY_SYSTEM:QESD_HISTORY_SYSTEM");
        statistics += checkValue("NOT_PR_HISTORY_SYSTEM:PR_HISTORY_SYSTEM");
        statistics += checkValue("NOT_ACK_HISTORY_SYSTEM:ACK_HISTORY_SYSTEM");
        statistics += checkValue("NOT_RSE_HISTORY_SYSTEM:RSE_HISTORY_SYSTEM");
        statistics += checkValue("NOT_ASN_HISTORY_USER:ASN_HISTORY_USER");
        statistics += checkValue("NOT_VSN_HISTORY_USER:VSN_HISTORY_USER");
        statistics += checkValue("NOT_SD_HISTORY_USER:SD_HISTORY_USER");
        statistics += checkValue("NOT_QESD_HISTORY_USER:QESD_HISTORY_USER");
        statistics += checkValue("NOT_PR_HISTORY_USER:PR_HISTORY_USER");
        statistics += checkValue("NOT_ACK_HISTORY_USER:ACK_HISTORY_USER");
        statistics += checkValue("NOT_RSE_HISTORY_USER:RSE_HISTORY_USER");
        map.clear();
        return statistics + "\n";
    }

    private String checkValue(String multipleKeys){
        String[] keys = multipleKeys.split(":");
        String result = "";
        for(String key : keys ){
            Integer value = map.get(key);
            result += "(" + key + ": " + (value == null || value.equals("null")? "0" : value) + ") ";
        }
        return "\t" + result;
    }

    public boolean checkFinished(){
        if( currentSheetIdx >= getNumSheets() ){
            return true;
        }
        return false;
    }

    public void writeComparison() {
        Sheet sheetComparison = workbook.getSheetAt( currentSheetIdx );
        if( sheetComparison.getSheetName().equals("Comparison") ) {
            CellStyle cs = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            cs.setFont(font);
            for( int sheetIdx = 0; sheetIdx < getNumSheets() - 1; sheetIdx++ ){
                Sheet sheet = workbook.getSheetAt( sheetIdx );
                //headers
                Row rowHeader = sheetComparison.getRow( rowOffset - 1 );
                rowHeader.createCell( sheetIdx + 1).setCellValue( sheet.getSheetName() );
                rowHeader.getCell( sheetIdx + 1).setCellStyle(cs);
                //values
                boolean isFarewell = false;
                for( int rowIdx = rowOffset; rowIdx < maxNumRows + 1 && !isFarewell; rowIdx++ ){
                    Row rowComparison = sheetComparison.getRow( rowIdx );
                    Row row = sheet.getRow( rowIdx );
                    if( row == null ){
                        row = sheet.createRow( rowIdx );
                    }
                    if( row.getCell(0) != null && row.getCell(0).getStringCellValue().equals(Constants.FAREWELL)){
                        isFarewell = true;
                    }
                    if (rowComparison.getCell(0) == null || rowComparison.getCell(0).getStringCellValue().equals("")) {
                        rowComparison.createCell(0).setCellValue(row.getCell(0) == null ? "" :
                                row.getCell(0).getStringCellValue());
                    }
                    rowComparison.createCell(sheetIdx + 1).setCellValue(row.getCell( colOffset ) == null ? "" :
                            row.getCell( colOffset ).getStringCellValue());
                }
            }
            writeAndClose();
        }
    }

    public void writeAndClose(){
        try {
            FileOutputStream fileOut = new FileOutputStream(docName);
            workbook.write(fileOut);
            fileOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getScenarioName() {
        return workbook.getSheetAt( currentSheetIdx ).getSheetName();
    }


    private void copyFile(File destFile) throws IOException {
        File sourceFile = new File("/Users/oscarr/Development/WEF/WEF-SocialReasoner/Execution/DavosResults/Davos.xlsx");
        if(!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    public String getFileName() {
        return docName;
    }

    public void processConvStrategies(){
        try {
            String path = "/Users/oscarr/Development/WEF/WEF-SocialReasoner/Execution/DavosResults/";
            File directory = new File(path);
            ArrayList<String> listOfFiles = null;
            if (directory.isDirectory()) {
                listOfFiles = new ArrayList(Arrays.asList(directory.list()));
            }
            //classify and move files accordingly
            for (String filePath : listOfFiles) {
                try {
                    if (!filePath.contains("Davos.xlsx") && !filePath.contains(".DS_Store") && !filePath.contains("DataPoints")
                            && !(new File(path + filePath).isDirectory()) && !filePath.startsWith("~")) {
                        openExcelDoc(path + filePath, false);
                        String dir = determineDirectory(extractUCS());
                        Files.move(Paths.get(path + filePath), Paths.get(path + dir + filePath));
                        reset();
                    }
                }catch (Exception e){
                }
            }


            //load intents to sentences file
            List<String[]> content = loadTSVfile(path +"intents_mapped_to_sentences.tsv");
            Multimap mappings = new ArrayListMultimap();
            for( String[] row : content ){
                String key = row[1] + "-" + row[2];
                mappings.put( key, new Realization(row[2], row[4]) );
            }

            for (String filePath : listOfFiles) {
                if (!filePath.contains("Davos.xlsx") && !filePath.contains(".DS_Store") && !filePath.contains("DataPoints")) {
                    openExcelDoc( path + filePath, false );
                    String[] key = extractKey().split(":");
                    Collection list = new ArrayList(mappings.get(key[0]));
                    list.isEmpty();
                    reset();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void reset(){
        workbook = null;
        excelSheet = null;
    }

    private String extractKey() {
        if( excelSheet == null ) {
            excelSheet = workbook.getSheetAt(currentSheetIdx);
        }
        Row row;
        String key = "";
        int lastRow = excelSheet.getLastRowNum();
        if( rowOffset < lastRow+1) {
            row = excelSheet.getRow(rowOffset);
            if (row != null && row.getCell(0) != null ) {
                key = (row.getCell(0).getStringCellValue() + "-" + row.getCell(6).getStringCellValue() + ":"
                        + row.getCell(0).getStringCellValue() + "-" + row.getCell(7).getStringCellValue())
                        .replace("null", "NONE");
            }
        }
        return key;
    }

    private List<CS> extractUCS(){
        if( excelSheet == null ) {
            excelSheet = workbook.getSheetAt(currentSheetIdx);
        }
        List<CS> css = new ArrayList<>();
        Row row;
        int lastRow = excelSheet.getLastRowNum();
        int posTemp = rowOffset;
        for( ; posTemp < lastRow+1; posTemp++ ) {
            row = excelSheet.getRow(posTemp);
            if (row != null && row.getCell(0) != null && !row.getCell(0).getStringCellValue().equals("")) {
                css.add( new CS(row.getCell(5).getStringCellValue(), row.getCell(24).getStringCellValue()) );
            }else{
                break;
            }
        }
        return css;
    }

    private String determineDirectory(List<CS> turns){
        String path;
        float userNONECount = 0, wozerNONECount = 0;
        for( CS cs : turns ){
            if( cs.userCS == null || cs.userCS.startsWith("NONE") || cs.userCS.startsWith("null") ){
                userNONECount++;
            }
            if( cs.wozerCS == null || cs.wozerCS.startsWith( "NONE" ) || cs.wozerCS.startsWith( "null" )  ){
                wozerNONECount++;
            }
        }
        if( userNONECount / turns.size() < .7 ){
            path = "with_ucs";
        }else{
            path = "without_ucs";
        }
        if( wozerNONECount / turns.size() < .7 ){
            path = path + "/with_wcs/";
        }else{
            path = path + "/without_wcs/";
        }
        System.out.println("User CS == NONE: " + userNONECount + ",  User CS != NONE: " + (turns.size()-userNONECount)
                + ", total: " + turns.size() + ", coef: " + (userNONECount/turns.size()));
        System.out.println("Wozer CS == NONE: " + wozerNONECount + ",  Wozer CS != NONE: " + (turns.size()-wozerNONECount)
                + ", total: " + turns.size() + ", coef: " + (wozerNONECount/turns.size()));
        return path;
    }


    public static List<String[]> loadTSVfile(String path){
        //load intents to sentences file
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        TsvParser parser = new TsvParser(settings);
        // parses all rows in one go.
        List<String[]> allRows = parser.parseAll( new File(path) );
        return allRows;
    }

    public static void main(String args[]){
        new ExcelReaderWriter().processConvStrategies();
    }

    static class Realization{
        String strategy;
        String utterance;

        public Realization(String strategy, String utterance) {
            this.strategy = strategy;
            this.utterance = utterance;
        }
    }

    class CS{
        String userCS;
        String wozerCS;

        public CS(String userCS, String wozerCS) {
            this.userCS = userCS;
            this.wozerCS = wozerCS;
        }
    }
}
