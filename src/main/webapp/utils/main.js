/**
 * Created by correnti on 16/11/2015.
 */

$(document).on({

    ready: function() {

        // Mobile section handler
        var mobile_section_moved = $("#mobile-section").width();
        $("#mobile-section").css("left", "-" + $("#mobile-section").css("width"));
        $("#mobile-btn").click(function () {
            $("body").animate({
                left: $("body").css("left") != "0px" ? "0px" : mobile_section_moved
            }, 200);
        });
        // end Mobile section handler



        $('#scrollToTop').click(function () {
            $('body, html').animate({ scrollTop: 0 }, 1000);
        });


        $(window).on({
            resize: _resize,
            scroll: function () {
               if ($(window).scrollTop() > 300) {
                    $('#scrollToTop').fadeIn(300);
                    return;
                }
                $('#scrollToTop').fadeOut(300);
            }
        });

        _resize();
        function _resize() {
            $("#mobile-section").height($(window).height() + "px");
        }

        $('#product_search_img').click(function (e) {
            $('#product_search').css("display", "inline-block");
            $('#product_search').animate({ "width": $('#product_search').width() > 0 ? 0 : "150px" }, 500, function(){
                if($('#product_search').width() == 0 ){
                    $(this).css("display", "none");
                }
            } );
        });


    },
});
