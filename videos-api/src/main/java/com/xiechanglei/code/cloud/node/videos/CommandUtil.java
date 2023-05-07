package com.xiechanglei.code.cloud.node.videos;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandUtil {
    public static String executeCommand(String[] command) throws Exception {

        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO();
            builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            Process p = builder.start();
            try (BufferedReader reader =new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            p.waitFor();
            p.destroy();
            return output.toString();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
