package org.chenmin.daka

class TxtController {

    def text


    def set(){
        text = params.text
    }

    def index() {
        if(text)
            render text
        render "1493cff504894e16d739d4a2e91adf32"
    }
}
