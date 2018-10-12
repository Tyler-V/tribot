package scripts.usa.api2007.training;

public class TrainingLevel {

    private int desiredLevel;
    private int experience;

    public TrainingLevel(int desiredLevel) {
	this.desiredLevel = desiredLevel;
	this.experience = 0;
    }

    public int getDesiredLevel() {
	return this.desiredLevel;
    }

    public int getNextExperience() {
	return this.experience;
    }

    public void setNextExperience(int experience) {
	this.experience = experience;
    }
}
