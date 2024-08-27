package com.yolo.customer.AI;

public class AIRequest {
    private String message;
    private Interests interests;
    private Restrictions restrictions;

    // Getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Interests getInterests() {
        return interests;
    }

    public void setInterests(Interests interests) {
        this.interests = interests;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }

    public static class Interests {
        private String interest1;
        private String interest2;
        private String interest3;

        // Getters and setters

        public String getInterest1() {
            return interest1;
        }

        public void setInterest1(String interest1) {
            this.interest1 = interest1;
        }

        public String getInterest2() {
            return interest2;
        }

        public void setInterest2(String interest2) {
            this.interest2 = interest2;
        }

        public String getInterest3() {
            return interest3;
        }

        public void setInterest3(String interest3) {
            this.interest3 = interest3;
        }
    }

    public static class Restrictions {
        private String restriction1;
        private String restriction2;
        private String restriction3;

        // Getters and setters

        public String getRestriction1() {
            return restriction1;
        }

        public void setRestriction1(String restriction1) {
            this.restriction1 = restriction1;
        }

        public String getRestriction2() {
            return restriction2;
        }

        public void setRestriction2(String restriction2) {
            this.restriction2 = restriction2;
        }

        public String getRestriction3() {
            return restriction3;
        }

        public void setRestriction3(String restriction3) {
            this.restriction3 = restriction3;
        }
    }
}
