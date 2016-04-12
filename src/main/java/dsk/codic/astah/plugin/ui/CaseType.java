package dsk.codic.astah.plugin.ui;

public class CaseType {

    private String value;
    private String displayValue;

    public CaseType(String value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }
    

    /*
    
    CAMEL {
        @Override
        public String getValue() {
            return "camel";
        }

        @Override
        public String getExample() {
            return "camelCase";
        }
    },
    PASCAL {
        @Override
        public String getValue() {
            return "pascal";
        }

        @Override
        public String getExample() {
            return "PascalCase";
        }
    },
    LOWER_UNDERSCORE {
        @Override
        public String getValue() {
            return "lower underscore";
        }

        @Override
        public String getExample() {
            return "snake_case";
        }
    },
    UPPER_UNDERSCORE {
        @Override
        public String getValue() {
            return "upper underscore";
        }

        @Override
        public String getExample() {
            return "SNAKE_CASE";
        }
    },
    HYPHEN {
        @Override
        public String getValue() {
            return "hyphen";
        }

        @Override
        public String getExample() {
            return "ハイフネーション（get-value）";
        }
    };
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.displayValue;
    }
}
