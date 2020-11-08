package koa.diridari.ffmpeg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import koa.diridari.Constants;
import koa.diridari.VideoStruct;

public class FfmpegOutput {

    private List<String> theLinesBetween = new ArrayList<>(256);;
    
    private String theOutputLine;

    private final Pattern patternVideoLine;
    private final Pattern patternResolution;
    private final Pattern patternType;

    public FfmpegOutput() throws Exception {
        String regExPatternVideoLine = (String)Constants.getProperties().get("ffmpeg.output.video_line_pattern");
        String regExPatternResolution = (String)Constants.getProperties().get("ffmpeg.output.resolution_pattern");
        String regExPatternType  = (String)Constants.getProperties().get("ffmpeg.output.type_pattern");
        patternVideoLine = Pattern.compile(regExPatternVideoLine);
        patternResolution = Pattern.compile(regExPatternResolution);        
        patternType = Pattern.compile(regExPatternType);
    }
    
    public void parseOutputOfConcatSingle(/*Path tmpPathOutput*/ List<String> result) throws IOException, FfmpegFailedException  {
        //List<String> result = Files.readAllLines(tmpPathOutput);
        Iterator<String> it = result.iterator();
        if (!it.hasNext()) {
            throw new FfmpegFailedException("output seems to be empty");
        }
        {
            boolean weHaveInput = false;
            // Step to input.
            do {
                String line = StringUtils.trimToEmpty(it.next());
                if (StringUtils.startsWith(line, "Input #0")) {
                    weHaveInput = true;
                }
                if (!it.hasNext()) {
                    throw new FfmpegFailedException("Nothing found for 'Input #0' or found in last line - very fishy incident.");
                }
            } while (!weHaveInput);
        }
        {
            boolean weHaveOutput = false;
            
            
            // Step to output and save the lines.
            do {
                theOutputLine = StringUtils.trimToEmpty(it.next());
                if (StringUtils.startsWith(theOutputLine, "Output #0")) {
                    weHaveOutput = true;
                } else {
                    theLinesBetween.add(theOutputLine);
                }
                if (!it.hasNext()) {
                    throw new FfmpegFailedException("Nothing found for 'Output #0' or found in last line - very fishy incident.");
                }
            } while (!weHaveOutput);
        }
        
    }

    public List<String> getTheLinesBetween() {
        return theLinesBetween;
    }

    public String getTheOutputLine() {
        return theOutputLine;
    }
    
    /**
     * Gets a structure containing some information about the video.
     * If the resolution of the video is not found horizental and vertical resolution is set to -1.
     * @see ffmpeg.output.video_line_pattern and ffmpeg.output.resolution_pattern in the configuration. 
     * @return A videostruct.
     * @throws Exception
     */
    public VideoStruct getVideoStruct() throws Exception {        
        int resolutionHorizontal = -1;
        int resolutionVertical = -1;
        String videoType = "none";
                
        for (String line : theLinesBetween) {            
            if (patternVideoLine.matcher(line).find()) {
                Matcher matcherResolution = patternResolution.matcher(line);
                if(matcherResolution.find()) {
                    String resolution = matcherResolution.group(0);                                        
                    resolutionHorizontal = Integer.valueOf(StringUtils.substringBefore(resolution, "x"));
                    resolutionVertical = Integer.valueOf(StringUtils.substringAfter(resolution, "x"));
                }
                Matcher matcherType = patternType.matcher(line);
                if(matcherType.find()) {
                    String type = matcherType.group(0);
                    String s = StringUtils.substringAfter(line, "Video: ");
                    s = StringUtils.substringBefore(s, "(");
                    videoType = StringUtils.trimToEmpty(s);
                }                
            }
        }
        return new VideoStruct(resolutionHorizontal, resolutionVertical, videoType);
    }
}
