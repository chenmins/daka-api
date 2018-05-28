package org.chenmin.daka

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/"(controller:"showRestApi")
        "/*.txt"(controller:"txt")
        "/*/*.txt"(controller:"txt")
        "/real"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
