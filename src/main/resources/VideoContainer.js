import React, {Component} from 'react';

class VideoContainer extends Component {
    render() {
        return (
            <video id="samp" width="640" height="480" controls playsInline autoPlay loop>
                <source src={"http://localhost:8080/video/stream/mp4/videoplayback"} type="video/mp4"/>
            </video>
        );
    }
}

export default VideoContainer;
