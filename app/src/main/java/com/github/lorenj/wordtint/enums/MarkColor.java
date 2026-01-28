package com.github.lorenj.wordtint.enums;

import com.github.lorenj.wordtint.R;

public enum MarkColor {
    /*
     fragment_word_credit中旗帜颜色定义的顺序一定要和这里的枚举类定义的顺序一致,否则会无法工作.
     */
    GREEN {
        @Override
        public int getMapColorID() {
            return R.color.theme_color;
        }
    }, RED {
        @Override
        public int getMapColorID() {
            return android.R.color.holo_red_dark;
        }
    }, ORANGE {
        @Override
        public int getMapColorID() {
            return android.R.color.holo_orange_dark;
        }
    }, YELLOW {
        @Override
        public int getMapColorID() {
            return R.color.holo_yellow_dark;
        }
    }, BLUE {
        @Override
        public int getMapColorID() {
            return android.R.color.holo_blue_dark;
        }
    }, CYAN {
        @Override
        public int getMapColorID() {
            return R.color.holo_cyan_dark;
        }
    }, PURPLE {
        @Override
        public int getMapColorID() {
            return android.R.color.holo_purple;
        }
    }, PINK {
        @Override
        public int getMapColorID() {
            return R.color.holo_pink_dark;
        }
    }, GRAY {
        @Override
        public int getMapColorID() {
            return R.color.dark_gray;
        }
    }, BLACK {
        @Override
        public int getMapColorID() {
            return android.R.color.black;
        }
    }, BROWN {
        @Override
        public int getMapColorID() {
            return R.color.halo_brown_dark;
        }
    };

    /**
     * 每个具体的枚举类都有它们各自对应的颜色,该颜色应该是colorID值,也就是说这里返回值是一个Color的引用ID值.
     *
     * @return @{@link R.id} 返回颜色值的引用.
     */
    public abstract int getMapColorID();

}
