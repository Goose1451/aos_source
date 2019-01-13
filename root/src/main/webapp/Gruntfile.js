/**
 * Created by kubany on 11/2/2015.
 */
module.exports = function (grunt) {
    'use strict';
    require('load-grunt-tasks')(grunt);
    grunt.loadNpmTasks('grunt-angular-templates');
    grunt.initConfig({
        requirejs: {
            js: {
                options: {
                    uglify2: {
                        mangle: false
                    },
                    baseUrl: "",
                    mainConfigFile: "main.js",
                    name: 'main',
                    out: "target/main.min.js",
                    optimize: 'none',
                    paths: {
                        'aos.templates': 'target/js/templates'
                    },
                    logLevel: 0
                }
            }
        },
        cssmin: {
            options: {
                shorthandCompacting: false,
                roundingPrecision: -1
            },
            target: {
                files: [
                    {
                        expand: true,
                        cwd: 'css',
                        src: ['*.css', '!*.min.css'],
                        dest: 'target/css',
                        ext: '.min.css'
                    }, {
                        expand: true,
                        cwd: 'app/user/css',
                        src: ['*.css', '!*.min.css'],
                        dest: 'target/app/user/css',
                        ext: '.min.css'
                    }, {
                        expand: true,
                        cwd: 'app/order/css',
                        src: ['*.css', '!*.min.css'],
                        dest: 'target/app/order/css',
                        ext: '.min.css'
                    }
                ]
            }
        },
        copy: {
            main: {
                files: [
                    // includes files within path
                    {
                        expand: true,
                        src: ['css/fonts/*', 'css/images/*', '!css/*.css'],
                        dest: 'target',
                        filter: 'isFile'
                    },
                    {
                        expand: true,
                        src: [
                            'app/views/*', 'app/partials/*', 'app/templates/*',
                            'app/user/views/*', 'app/user/partials/*',
                            'app/order/views/*', 'app/order/partials/*',
                            'app/account/views/*', 'app/account/partials/*',
                        ],
                        dest: 'target',
                        filter: 'isFile'
                    },
                    {
                        expand: true,
                        src: [
                            'scripts/nv_files/*'
                        ],
                        dest: 'target',
                        filter: 'isFile'
                    },
                    {
                        expand: true,
                        src: ['index.html',
                            'app/tempFiles/popularProducts.json',
                            'css/fonts/roboto_thin_macroman/Roboto-Thin-webfont.eot',
                            'css/fonts/roboto_thin_macroman/Roboto-Thin-webfont.svg',
                            'css/fonts/roboto_thin_macroman/Roboto-Thin-webfont.ttf',
                            'css/fonts/roboto_thin_macroman/Roboto-Thin-webfont.woff',
                            'css/fonts/roboto_light_macroman/Roboto-Light-webfont.eot',
                            'css/fonts/roboto_light_macroman/Roboto-Light-webfont.svg',
                            'css/fonts/roboto_light_macroman/Roboto-Light-webfont.ttf',
                            'css/fonts/roboto_light_macroman/Roboto-Light-webfont.woff',
                            'css/fonts/roboto_regular_macroman/Roboto-Regular-webfont.eot',
                            'css/fonts/roboto_regular_macroman/Roboto-Regular-webfont.svg',
                            'css/fonts/roboto_regular_macroman/Roboto-Regular-webfont.ttf',
                            'css/fonts/roboto_regular_macroman/Roboto-Regular-webfont.woff',
                            'css/fonts/roboto_medium_macroman/Roboto-Medium-webfont.eot',
                            'css/fonts/roboto_medium_macroman/Roboto-Medium-webfont.svg',
                            'css/fonts/roboto_medium_macroman/Roboto-Medium-webfont.ttf',
                            'css/fonts/roboto_medium_macroman/Roboto-Medium-webfont.woff',
                            'css/fonts/roboto_bold_macroman/Roboto-Bold-webfont.eot',
                            'css/fonts/roboto_bold_macroman/Roboto-Bold-webfont.svg',
                            'css/fonts/roboto_bold_macroman/Roboto-Bold-webfont.ttf',
                            'css/fonts/roboto_bold_macroman/Roboto-Bold-webfont.woff',
                            'css/chat/chat.css',
                        ],
                        dest: 'target',
                        filter: 'isFile'
                    },
                    {
                        expand: true,
                        src: ['css/videos/*',],
                        dest: 'target',
                    },

                ]
            }
        },
        ngtemplates: {
            app: {
                src: ['app/partials/**.html',
                    'app/user/partials/**.html',
                    'app/order/partials/**.html',
                    'app/account/partials/**.html',
                ],
                dest: 'app/templates/module.js',
                options: {
                    bootstrap: function (module, script) {
                        return '\
                        define(["angular"], function (angular) {\
                            "use strict";\
                            var templates = angular.module("aos.templates", []);\
                            templates.run(function($templateCache) {\
                            ' + script + '\
                            });\
                            return templates;\
                        });';
                    }
                }
            }

        },
        clean: ["target", "app/templates"],
        usemin: {
            html: ['target/index.html']
        },
        'regex-replace': {
            dist: {
                src: ['target/index.html'],
                dest: 'target',
                actions: [
                    {
                        name: 'requirejs-onefile',
                        search: '<script type="text/javascript" data-main="main" src="vendor/requirejs/require.js"></script>',
                        replace: '<script type="text/javascript" data-main="main.min" src="vendor/requirejs/require.js"></script>'
                    }
                ]
            }
        },
        watch: {
            scripts: {
                files: ['app/**/*.html'],
                tasks: ['ngTemplatesBuild', 'ngt'],
                options: {
                    spawn: false
                }
            }
        },

        htmlmin: {                                     // Task
            dist: {                                      // Target
                options: {                                 // Target options
                    removeComments: true,
                    collapseWhitespace: true
                },
                files: {                                   // Dictionary of files
                    'target/index.html': 'target/index.html'    // 'destination': 'source'
                }
            }

        }




    });
    grunt.registerTask('default', ['clean', 'ngtemplates', 'requirejs', 'copy', 'regex-replace', 'usemin', 'cssmin']);
    grunt.registerTask('dev', ['clean', 'ngtemplates']);
    grunt.registerTask('ngTemplatesBuild', ['ngtemplates']);
    grunt.registerTask('ngt', ['ngtemplates']);
    grunt.registerTask('useminTest', ['copy', 'regex-replace', 'usemin']);
    grunt.registerTask('htmlminTest', ['htmlmin']);

};


//'app/SECORRENTI/sec-input/views/sec-input-validation.html',
