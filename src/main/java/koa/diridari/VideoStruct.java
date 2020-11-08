package koa.diridari;

/**
 * Holds some characteristics of a video.
 * 
 * @author user
 *
 */
public class VideoStruct {
	
	public final int resolutionHorizontal;
	public final int resolutionVertical;
	public final String type;
	
	public VideoStruct(int resolutionHorizontal, int resolutionVertical, String type) {
		this.resolutionVertical = resolutionVertical;
		this.resolutionHorizontal = resolutionHorizontal;	
		this.type = type;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + resolutionHorizontal;
        result = prime * result + resolutionVertical;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VideoStruct other = (VideoStruct) obj;
        if (resolutionHorizontal != other.resolutionHorizontal)
            return false;
        if (resolutionVertical != other.resolutionVertical)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }   
}
