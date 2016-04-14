package pacman.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import pacman.game.Constants.MOVE;
import java.util.*;

/**
 * Read data (Game state) from previously saved txt file
 * Created by Yu Liao on 4/13/16.
 */
public class ReadData {

    /**
     * Read game state data set from a txt file
     * @param path
     * @return
     */
    public List<DataPoint> read (String path) {

        List<DataPoint> dataList = new ArrayList<DataPoint>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            StringBuffer sb = new StringBuffer();
            String line = br.readLine();

            while (line != null) {

                // convert data to corresponding data type
                DataPoint dataPoint = new DataPoint();
                String[] data = line.split(" ");

                dataPoint.setClosestPill(Integer.parseInt(data[0]));
                dataPoint.setClosestGhost(Integer.parseInt(data[1]));
                dataPoint.setIslair(Integer.parseInt(data[2]) == 1);
                dataPoint.setToPill(Integer.parseInt(data[3]) == 1);
                dataPoint.setAwayGhost(Integer.parseInt(data[4]) == 1);

                dataList.add(dataPoint);

                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
//            String fileData = sb.toString();
            // System.out.print(fileData);

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    /**
     * This is for test
     * @param args
     */
    public static void main(String[] args) {

        String path = "/home/liaoyu/workspace/Pacman2/data/data1.txt";

        ReadData readData = new ReadData();
        List<DataPoint> data = readData.read(path);
        //  for (int i = 0; i < data.size(); i++) {
        //     System.out.println(data.get(i).getScore());
        //  }

        // System.out.print(data.get(100).getScore());
    }

}

