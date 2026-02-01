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

        @Override
        public int order() {
            return 0;
        }
    },
    RED {
        @Override
        public int getMapColorID() {
            return android.R.color.holo_red_dark;
        }

        @Override
        public int order() {
            return 1;
        }
    },
    ORANGE {
        @Override
        public int getMapColorID() {
            return android.R.color.holo_orange_dark;
        }

        @Override
        public int order() {
            return 2;
        }
    },
    YELLOW {
        @Override
        public int getMapColorID() {
            return R.color.holo_yellow_dark;
        }

        @Override
        public int order() {
            return 3;
        }
    },
    BLUE {
        @Override
        public int getMapColorID() {
            return android.R.color.holo_blue_dark;
        }

        @Override
        public int order() {
            return 4;
        }
    },
    CYAN {
        @Override
        public int getMapColorID() {
            return R.color.holo_cyan_dark;
        }

        @Override
        public int order() {
            return 5;
        }
    },
    PURPLE {
        @Override
        public int getMapColorID() {
            return android.R.color.holo_purple;
        }

        @Override
        public int order() {
            return 6;
        }
    },
    PINK {
        @Override
        public int getMapColorID() {
            return R.color.holo_pink_dark;
        }

        @Override
        public int order() {
            return 7;
        }
    },
    GRAY {
        @Override
        public int getMapColorID() {
            return R.color.dark_gray;
        }

        @Override
        public int order() {
            return 8;
        }
    },
    BLACK {
        @Override
        public int getMapColorID() {
            return android.R.color.black;
        }

        @Override
        public int order() {
            return 9;
        }
    },
    BROWN {
        @Override
        public int getMapColorID() {
            return R.color.halo_brown_dark;
        }

        @Override
        public int order() {
            return 10;
        }
    };

    /**
     * 每个具体的枚举类都有它们各自对应的颜色,该颜色应该是colorID值,也就是说这里返回值是一个Color的引用ID值.
     *
     * @return @{@link R.id} 返回颜色值的引用.
     */
    public abstract int getMapColorID();

    public abstract int order();

    public static MarkColor valueOfName(String name) {
        if (name == null) return null;
        for (MarkColor e : MarkColor.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }


}
